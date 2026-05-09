package com.example.nammapustaka.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammapustaka.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = LibraryDatabase.getDatabase(application).dao()

    val allBooks: StateFlow<List<BookEntity>> = dao.getAllBooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val allStudents: StateFlow<List<StudentEntity>> = dao.getAllStudents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val allTransactions: StateFlow<List<TransactionEntity>> = dao.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchedStudents: StateFlow<List<StudentEntity>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isEmpty()) flowOf(emptyList())
            else dao.searchStudents(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addBook(title: String, author: String, category: String, code: String) {
        viewModelScope.launch {
            dao.insertBook(BookEntity(
                title = title, 
                author = author, 
                category = category, 
                coverUrl = "", 
                bookCode = code
            ))
        }
    }

    fun addStudent(name: String, studentId: String, className: String) {
        viewModelScope.launch {
            dao.insertStudent(StudentEntity(name = name, studentId = studentId, className = className))
        }
    }

    suspend fun getBookByCode(code: String): BookEntity? {
        return dao.getBookByCode(code)
    }

    suspend fun issueBook(book: BookEntity, student: StudentEntity): Boolean {
        return try {
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

    suspend fun returnBook(book: BookEntity): Boolean {
        val transaction = dao.getActiveTransactionForBook(book.id)
        return if (transaction != null) {
            dao.updateTransaction(transaction.copy(
                returnDate = System.currentTimeMillis(), 
                returned = true
            ))
            dao.updateBook(book.copy(isIssued = false))
            true
        } else {
            false
        }
    }

    // Keep legacy for compatibility if needed
    fun processScannedCode(code: String, studentId: Long, studentName: String): Boolean {
        return false
    }
}