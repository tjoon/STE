package comtjoon.github.ste.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import comtjoon.github.ste.R
import comtjoon.github.ste.model.Response
import kotlinx.android.synthetic.main.fragment_register.*
import rx.subscriptions.CompositeSubscription
import comtjoon.github.ste.model.User
import comtjoon.github.ste.utils.Validation.Companion.validateEmail
import comtjoon.github.ste.utils.Validation.Companion.validateFields
import rx.schedulers.Schedulers
import rx.android.schedulers.AndroidSchedulers
import comtjoon.github.ste.network.NetworkUtil
import android.support.design.widget.Snackbar
import retrofit2.adapter.rxjava.HttpException
import com.google.gson.GsonBuilder
import java.io.IOException


class RegisterFragment : Fragment(){
    companion object {
        val TAG = RegisterFragment::class.java.simpleName
    }

    private var mSubscriptions: CompositeSubscription? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_register, container, false)
        mSubscriptions = CompositeSubscription()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun initView(v : View){
        btn_register.setOnClickListener {
            v -> register()
        }
        tv_login.setOnClickListener {
            v -> goToLogin()
        }
    }


    private fun register() {

        setError()

        val name = et_name.getText().toString()
        val email = et_email.getText().toString()
        val password = et_password.getText().toString()

        var err = 0

        if (!validateFields(name)) {

            err++
            ti_name.setError("Name should not be empty !")
        }

        if (!validateEmail(email)) {

            err++
            ti_email.setError("Email should be valid !")
        }

        if (!validateFields(password)) {

            err++
            ti_password.setError("Password should not be empty !")
        }

        if (err == 0) {

            var user = User()
            user.setName(name)
            user.setEmail(email)
            user.setPassword(password)

            progress.setVisibility(View.VISIBLE)
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
                .subscribe(this::handleResponse,this::handleError));
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

            val gson = GsonBuilder().create()

            try {

                val errorBody = error.response().errorBody().string()
                val response = gson.fromJson(errorBody, Response::class.java)
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

}