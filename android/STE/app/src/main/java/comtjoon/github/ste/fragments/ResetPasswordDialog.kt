package comtjoon.github.ste.fragments


import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.GsonBuilder
import comtjoon.github.ste.MainActivity
import comtjoon.github.ste.R
import comtjoon.github.ste.model.Response
import comtjoon.github.ste.model.User
import comtjoon.github.ste.network.NetworkUtil
import comtjoon.github.ste.utils.Validation.Companion.validateEmail
import comtjoon.github.ste.utils.Validation.Companion.validateFields
import kotlinx.android.synthetic.main.dialog_reset_password.*
import kotlinx.android.synthetic.main.dialog_reset_password.view.*
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.io.IOException


open class ResetPasswordDialog : DialogFragment() {

    private var mSubscriptions: CompositeSubscription? = null
    private var mEmail: String? = null
    private var isInit = true

    private var mListner: Listener? = null

    interface Listener {

        fun onPasswordReset(message: String)
    }

    companion object {
        val TAG = ResetPasswordDialog::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.dialog_reset_password, container, false)
        mSubscriptions = CompositeSubscription()
        initViews(view);
        return view
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListner = context as MainActivity
    }

    private fun setEmptyFields() {

        ti_email.error = null
        ti_token.error = null
        ti_password.error = null
        tv_message.text = null
    }

    fun setToken(token: String) {

        et_token.setText(token)
    }

    private fun initViews(v: View) {
        v.btn_reset_password.setOnClickListener {
            if (isInit)
                resetPasswordInit()
            else
                resetPasswordFinish()
        }
    }

    private fun resetPasswordInit() {

        setEmptyFields()

        mEmail = et_email.getText().toString()

        var err = 0

        if (!validateEmail(mEmail!!)) {

            err++
            ti_email.setError("Email Should be Valid !")
        }

        if (err == 0) {

            progress.setVisibility(View.VISIBLE)
            resetPasswordInitProgress(mEmail!!)
        }
    }

    private fun resetPasswordFinish() {

        setEmptyFields()

        val token = et_token.getText().toString()
        val password = et_password.getText().toString()

        var err = 0

        if (!validateFields(token)) {

            err++
            ti_token.setError("Token Should not be empty !")
        }

        if (!validateFields(password)) {

            err++
            ti_email.setError("Password Should not be empty !")
        }

        if (err == 0) {

            progress.setVisibility(View.VISIBLE)

            val user = User()
            user.setPassword(password)
            user.setToken(token)
            resetPasswordFinishProgress(user)
        }
    }


    private fun resetPasswordInitProgress(email: String) {

        mSubscriptions!!.add(NetworkUtil.getRetrofit().resetPasswordInit(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private fun resetPasswordFinishProgress(user: User) {

        mSubscriptions!!.add(NetworkUtil.getRetrofit().resetPasswordFinish(mEmail!!, user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private fun handleResponse(response: Response) {

        progress.setVisibility(View.GONE)

        if (isInit) {

            isInit = false
            showMessage(response.getMessage()!!)
            ti_email.setVisibility(View.GONE)
            ti_token.setVisibility(View.VISIBLE)
            ti_password.setVisibility(View.VISIBLE)

        } else {

            mListner!!.onPasswordReset(response.getMessage()!!)
            dismiss()
        }
    }

    private fun handleError(error: Throwable) {

        progress.setVisibility(View.GONE)

        if (error is HttpException) {

            val gson = GsonBuilder().create()

            try {

                val errorBody = error.response().errorBody().string()
                val response = gson.fromJson(errorBody, Response::class.java)
                showMessage(response.getMessage()!!)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {

            showMessage("Network Error !")
        }
    }

    private fun showMessage(message: String) {

        tv_message.setVisibility(View.VISIBLE)
        tv_message.setText(message)

    }

}