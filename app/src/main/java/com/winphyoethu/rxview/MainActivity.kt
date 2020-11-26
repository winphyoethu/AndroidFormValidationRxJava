package com.winphyoethu.rxview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function3
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private val rxViewObservable = RxViewObservable()

    private val compositeDisposable = CompositeDisposable()

    private lateinit var formObservable: Observable<Boolean>
    private lateinit var buttonObservable: Observable<Unit>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonObservable = rxViewObservable.fromButton(btnSubmit)
            .debounce(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                showSuccessDialog()
            }

        // Use
        // rxViewObservable.fromEditText(etName).startWith("")
        // rxViewObservable.fromEditText(etEmail).startWith("")
        // rxViewObservable.fromEditText(etPassword).startWith("")
        // if want to observe from the start of the data stream with default value

        formObservable = Observable.combineLatest(
            rxViewObservable.fromEditText(etName),
            rxViewObservable.fromEditText(etEmail),
            rxViewObservable.fromEditText(etPassword),
            Function3<String, String, String, Boolean> { name, email, password ->

                val isNameValid = name.isNotEmpty()
                if (!isNameValid) {
                    tipName.error = "Please fill Name"
                } else {
                    tipName.error = ""
                }

                var isEmailValid = email.isNotEmpty()
                if (!isEmailValid) {
                    tipEmail.error = "Please fill Email"
                } else {
                    val pattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
                    val matcher = pattern.matcher(email)
                    if (matcher.matches()) {
                        tipEmail.error = ""
                        isEmailValid = true
                    } else {
                        tipEmail.error = "The mail you entered is not email"
                        isEmailValid = false
                    }
                }

                val isPasswordValid = password.isNotEmpty()
                if (!isPasswordValid) {
                    tipPassword.error = "Please fill Password"
                } else {
                    tipPassword.error = ""
                }

                isNameValid && isEmailValid && isPasswordValid
            })
            .doOnNext {
                btnSubmit.isEnabled = it
            }
    }

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Message")
            .setMessage("Thank you for filling the form.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }

    override fun onResume() {
        super.onResume()
        compositeDisposable.add(formObservable.subscribe())
        compositeDisposable.add(buttonObservable.subscribe())
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}