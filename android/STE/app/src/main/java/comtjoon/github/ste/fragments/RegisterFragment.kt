package comtjoon.github.ste.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.GsonBuilder
import comtjoon.github.ste.R
import comtjoon.github.ste.model.Response
import comtjoon.github.ste.model.User
import comtjoon.github.ste.network.NetworkUtil
import comtjoon.github.ste.utils.Validation.Companion.validateEmail
import comtjoon.github.ste.utils.Validation.Companion.validateFields
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.view.*
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.io.IOException


class RegisterFragment : Fragment() {
    companion object {
        val TAG = RegisterFragment::class.java.simpleName
    }

    private var mSubscriptions: CompositeSubscription? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_register, container, false)
        mSubscriptions = CompositeSubscription()
        initViews(view);
        return view
    }

    private fun initViews(v: View) {
        v.btn_register.setOnClickListener {
            register()
        }
        v.tv_login.setOnClickListener {
            goToLogin()
        }
    }


    private fun register() {

        setError()

        var name = et_name.getText().toString()
        var email = et_email.getText().toString()
        var password = et_password.getText().toString()

        var err = 0

        if (!validateFields(name)) {

            err++
            ti_name.error = "Name should not be empty !"
        }

        if (!validateEmail(email)) {

            err++
            ti_email.error = "Email should be valid !"
        }

        if (!validateFields(password)) {

            err++
            ti_password.error = "Password should not be empty !"
        }

        if (err == 0) {

            var user = User()
            user.setName(name)
            user.setEmail(email)
            user.setPassword(password)

            progress.visibility = View.VISIBLE
            registerProcess(user)

        } else {

            showSnackBarMessage("Enter Valid Details !")
        }
    }

    private fun setError() {

        ti_name.setError(null)
        ti_email.setError(null)
        ti_password.setError(null)
    }

    private fun registerProcess(user: User) {

        mSubscriptions!!.add(NetworkUtil.getRetrofit().register(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }

    private fun handleResponse(response: Response) {
        progress.setVisibility(View.GONE)
        showSnackBarMessage(response.getMessage()!!)
    }

    private fun showSnackBarMessage(message: String) {

        if (view != null) {

            Snackbar.make(view!!, message, Snackbar.LENGTH_SHORT).show()
        }
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

    private fun goToLogin() {

        var ft = fragmentManager!!.beginTransaction()
        var fragment = LoginFragment()
        ft.replace(R.id.fragmentFrame, fragment, LoginFragment.TAG)
        ft.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSubscriptions!!.unsubscribe()
    }

}