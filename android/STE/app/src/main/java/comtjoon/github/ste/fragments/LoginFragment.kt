package comtjoon.github.ste.fragments


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.gson.GsonBuilder
import comtjoon.github.ste.ProfileActivity
import comtjoon.github.ste.R
import comtjoon.github.ste.R.layout.fragment_login
import comtjoon.github.ste.model.Response
import comtjoon.github.ste.network.NetworkUtil
import comtjoon.github.ste.utils.Constants
import comtjoon.github.ste.utils.Validation
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.io.IOException





class LoginFragment : Fragment() {

    companion object {
        val TAG = LoginFragment::class.java.simpleName
    }

    private var mBtLogin: Button? = null

    private var mSubscriptions: CompositeSubscription? = null
    private var mSharedPreferences: SharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(fragment_login, container, false)
        mSubscriptions = CompositeSubscription()
        initViews(view)
        initSharedPreferences()
        return view
    }

    private fun initViews(v: View) {
        btn_login?.setOnClickListener {
            login()
        }
        tv_register?.setOnClickListener{
            goToRegister()
        }
        tv_forgot_password?.setOnClickListener{
            showDialog()
        }

    }

    private fun initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
    }

    private fun login() {
        setError()

        var email: String = et_email.text.toString()
        var password: String = et_password.text.toString()

        var err: Int = 0

        if (!Validation.validateEmail(email)) {
            err++
            ti_email.error = "Email Should be Valid !"
        }

        if (!Validation.validateFields(password)) {
            err++
            ti_password.setError("Password should not be empty !")
        }


        if (err == 0) {
        }

    }

    private fun setError() {
        ti_email.error = null
        ti_password.error = null
    }

    private fun loginProcess(email: String, password: String) {
        mSubscriptions!!.add(NetworkUtil.getRetrofit(email, password).login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private fun handleResponse(response: Response) {

        progress.setVisibility(View.GONE)

        var editor = mSharedPreferences!!.edit()
        editor.putString(Constants.TOKEN, response.getToken())
        editor.putString(Constants.EMAIL, response.getMessage())
        editor.apply()

        et_email.setText(null)
        et_password.setText(null)

        val intent = Intent(activity, ProfileActivity::class.java)
        startActivity(intent)

    }

    private fun handleError(error: Throwable) {

        progress.setVisibility(View.GONE)

        if (error is HttpException) {

            var gson = GsonBuilder().create()

            try {

                var errorBody = error.response().errorBody().string()
                var response = gson.fromJson(errorBody, Response::class.java)
                showSnackBarMessage(response.getMessage()!!)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {

            showSnackBarMessage("Network Error !")
        }
    }

    private fun showSnackBarMessage(message: String) {

        if (view != null) {

            Snackbar.make(view!!, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun goToRegister() {

        var ft = fragmentManager!!.beginTransaction()
        var fragment = RegisterFragment()
        ft.replace(R.id.fragmentFrame, fragment, RegisterFragment.TAG)
        ft.commit()
    }

    private fun showDialog() {

        val fragment = ResetPasswordDialog()
        fragment.show(fragmentManager, ResetPasswordDialog.TAG)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSubscriptions!!.unsubscribe();
    }


}