package comtjoon.github.ste.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import comtjoon.github.ste.MainActivity
import comtjoon.github.ste.ProfileActivity
import comtjoon.github.ste.R
import comtjoon.github.ste.model.Response
import comtjoon.github.ste.model.User
import comtjoon.github.ste.network.NetworkUtil
import comtjoon.github.ste.utils.Constants
import comtjoon.github.ste.utils.Validation.Companion.validateFields
import kotlinx.android.synthetic.main.dialog_withdraw.*
import kotlinx.android.synthetic.main.dialog_withdraw.view.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

class WithdrawDialog : DialogFragment(){

    interface Listener {

        fun onWithdrawFinish(message: String)
    }

    companion object {
        val TAG = WithdrawDialog::class.java.simpleName
    }

    private var mSubscriptions: CompositeSubscription? = null
    private var mToken: String? = null
    private var mEmail: String? = null
    private var mListener: Listener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.dialog_withdraw,container,false)
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
        v.btn_withdraw.setOnClickListener{
            withdraw()
        }
        v.btn_cancel.setOnClickListener{
            dismiss()
        }
    }

    private fun withdraw() {

        setError()

        val currentPassword : String = et_current_password.text.toString()
        val confirmPassword : String = et_confirm_password.text.toString()

        var err = 0

        if (!validateFields(currentPassword)) {

            err++
            ti_current_password.error = "Password should not be empty !"
        }

        if (!validateFields(confirmPassword)) {

            err++
            ti_confirm_password.error = "Password should not be empty !"
        }

        if(err == 0){

            var user = User()
            user.setPassword(currentPassword)
            user.setConfirmPassword(confirmPassword)
            withdrawProgress(user)
            progress.visibility = View.VISIBLE
        }
    }

    private fun setError() {

        ti_current_password.error = null
        ti_confirm_password.error = null
    }

    private fun withdrawProgress(user: User){
        mSubscriptions!!.add(NetworkUtil.getRetrofit(mToken!!).withdraw(mEmail!!, user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError))
    }

    private fun handleResponse(response: Response) {
        progress.visibility = View.GONE
        Toast.makeText(context,response.getMessage(),Toast.LENGTH_LONG).show()
        mListener!!.onWithdrawFinish(response.getMessage()!!)
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        dismiss()

    }
    private fun handleError(error: Throwable) {
        progress.visibility = View.GONE
        Toast.makeText(context,error.message,Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSubscriptions!!.unsubscribe()
    }
}