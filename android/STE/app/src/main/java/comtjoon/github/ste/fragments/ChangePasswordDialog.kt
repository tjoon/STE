package comtjoon.github.ste.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.GsonBuilder
import comtjoon.github.ste.ProfileActivity
import comtjoon.github.ste.R
import comtjoon.github.ste.model.Response
import comtjoon.github.ste.model.User
import comtjoon.github.ste.network.NetworkUtil
import comtjoon.github.ste.utils.Constants
import comtjoon.github.ste.utils.Validation.Companion.validateFields
import kotlinx.android.synthetic.main.dialog_change_password.*
import kotlinx.android.synthetic.main.dialog_change_password.view.*
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.io.IOException




open class ChangePasswordDialog : DialogFragment(){

    interface Listener {

        fun onPasswordChanged()
    }

    companion object {
        val TAG = ChangePasswordDialog::class.java.simpleName
    }

    private var mSubscriptions: CompositeSubscription? = null
    private var mToken: String? = null
    private var mEmail: String? = null
    private var mListener: Listener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.dialog_change_password, container, false)
        mSubscriptions = CompositeSubscription()
        getData()
        initViews(view)
        return view
    }


    private fun getData() {

        val bundle = arguments

        mToken = bundle!!.getString(Constants.TOKEN)
        mEmail = bundle.getString(Constants.EMAIL)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as ProfileActivity
    }

    private fun initViews(v: View) {
        v.btn_change_password.setOnClickListener{
            changePassword()
        }
        v.btn_cancel.setOnClickListener{
            dismiss()
        }
    }

    private fun changePassword() {

        setError()

        val oldPassword = et_old_password.text.toString()
        val newPassword = et_new_password.text.toString()

        var err = 0

        if (!validateFields(oldPassword)) {

            err++
            ti_old_password.error = "Password should not be empty !"
        }

        if (!validateFields(newPassword)) {

            err++
            ti_new_password.error = "Password should not be empty !"
        }

        if (err == 0) {

            var user = User()
            user.setPassword(oldPassword)
            user.setNewPassword(newPassword)
            changePasswordProgress(user)
            progress.visibility = View.VISIBLE

        }
    }

    private fun setError() {

        ti_old_password.error = null
        ti_new_password.error = null
    }

    private fun changePasswordProgress(user: User) {

        mSubscriptions!!.add(NetworkUtil.getRetrofit(mToken!!).changePassword(mEmail!!, user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError))
    }

    private fun handleResponse(response: Response) {

        progress.visibility = View.GONE
        mListener!!.onPasswordChanged()
        dismiss()
    }

    private fun handleError(error: Throwable) {

        progress.visibility = View.GONE

        if (error is HttpException) {

            var gson = GsonBuilder().create()

            try {

                var errorBody = error.response().errorBody().string()
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

        tv_message.visibility = View.VISIBLE
        tv_message.text = message

    }

    override fun onDestroy() {
        super.onDestroy()
        mSubscriptions!!.unsubscribe()
    }


}