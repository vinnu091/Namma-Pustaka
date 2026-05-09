package com.example.vinnu.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinnu.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = LibraryDatabase.getDatabase(application).dao()

    val allBooks: StateFlow<List<BookEntity>> = dao.getAllBooks()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        
    val allStudents: StateFlow<List<StudentEntity>> = dao.getAllStudents()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        
    val allTransactions: StateFlow<List<TransactionEntity>> = dao.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchedStudents: StateFlow<List<StudentEntity>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isEmpty()) flowOf(emptyList())
            else dao.searchStudents(query)
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaderboard: StateFlow<List<Pair<String, Int>>> = allTransactions
        .map { transactions ->
            transactions.filter { it.returned } 
                .groupBy { it.studentName }
                .map { (name, list) -> name to list.sumOf { it.pagesRead } }
                .sortedByDescending { it.second }
        }
        .flowOn(Dispatchers.Default) // Fix: Ensure heavy calculation is off the main thread
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addBook(title: String, author: String, category: String, code: String, pages: Int = 100) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertBook(BookEntity(
                title = title, 
                author = author, 
                category = category, 
                coverUrl = "", 
                bookCode = code.trim(),
                pages = pages
            ))
        }
    }

    fun addStudent(name: String, studentId: String, className: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertStudent(StudentEntity(name = name, studentId = studentId, className = className))
        }
    }

    fun addReview(bookId: Long, studentName: String, rating: Int, comment: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertReview(ReviewEntity(bookId = bookId, studentName = studentName, rating = rating, comment = comment))
        }
    }
    
    fun getReviews(bookId: Long): Flow<List<ReviewEntity>> = dao.getReviewsForBook(bookId)

    suspend fun getBookByCode(code: String): BookEntity? {
        return withContext(Dispatchers.IO) {
            dao.getBookByCode(code.trim())
        }
    }

    suspend fun issueBook(book: BookEntity, student: StudentEntity): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                dao.insertTransaction(TransactionEntity(
                    bookId = book.id,
                    studentId = student.id,
                    studentName = student.name,
                    bookTitle = book.title,
                    borrowDate = System.currentTimeMillis()
                ))
                dao.updateBook(book.copy(isIssued = true))
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    suspend fun returnBook(book: BookEntity): Boolean {
        return withContext(Dispatchers.IO) {
            val transaction = dao.getActiveTransactionForBook(book.id)
            if (transaction != null) {
                dao.updateTransaction(transaction.copy(
                    returnDate = System.currentTimeMillis(), 
                    returned = true,
                    pagesRead = book.pages
                ))
                dao.updateBook(book.copy(isIssued = false))
                true
            } else {
                false
            }
        }
    }

    suspend fun processScannedCode(code: String, studentId: Long, studentName: String): Boolean {
        val book = getBookByCode(code) ?: return false
        return if (!book.isIssued) {
            val student = dao.getAllStudents().first().find { it.id == studentId } ?: return false
            issueBook(book, student)
        } else {
            returnBook(book)
        }
    }
}