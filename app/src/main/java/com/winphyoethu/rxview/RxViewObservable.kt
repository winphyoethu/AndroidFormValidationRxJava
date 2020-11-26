package com.winphyoethu.rxview

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class RxViewObservable {

    fun fromEditText(editText: EditText): Observable<String> {

        val editTextSubject: PublishSubject<String> = PublishSubject.create()

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editTextSubject.onNext(s.toString())
            }
        })

        return editTextSubject

    }

    fun fromButton(button: Button): Observable<Unit> {

        val buttonSubject: PublishSubject<Unit> = PublishSubject.create()

        button.setOnClickListener {
            buttonSubject.onNext(Unit)
        }

        return buttonSubject

    }

    fun fromSearchView(searchView: SearchView): Observable<String> {

        val searchViewSubject: PublishSubject<String> = PublishSubject.create()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchViewSubject.onNext(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchViewSubject.onNext(it)
                }
                return true
            }
        })

        return searchViewSubject
    }

}