package comtjoon.github.ste

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.gson.GsonBuilder
import comtjoon.github.ste.fragments.ChangePasswordDialog
import comtjoon.github.ste.fragments.WithdrawDialog
import comtjoon.github.ste.model.Response
import comtjoon.github.ste.model.User
import comtjoon.github.ste.network.NetworkUtil
import comtjoon.github.ste.utils.Constants
import kotlinx.android.synthetic.main.activity_profile.*
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.io.IOException


class ProfileActivity : AppCompatActivity(), ChangePasswordDialog.Listener, WithdrawDialog.Listener {

    companion object {
        val TAG = ProfileActivity::class.java.simpleName
    }

    private var mSubscriptions: CompositeSubscription? = null
    private var mSharedPreferences: SharedPreferences? = null
    private var mToken: String? = null
    private var mEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        mSubscriptions = CompositeSubscription()
        initViews()
        initSharedPreferences()
        loadProfile()
    }

    private fun initViews() {
        btn_change_password.setOnClickListener {
            showDialog()
        }
        btn_logout.setOnClickListener {
            logout()
        }
        btn_withdraw.setOnClickListener {
            showDialog_withdraw()
        }
    }

    private fun initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        mToken = mSharedPreferences!!.getString(Constants.TOKEN, "")
        mEmail = mSharedPreferences!!.getString(Constants.EMAIL, "")
    }

    private fun logout() {

        var editor = mSharedPreferences!!.edit()
        editor.putString(Constants.EMAIL, "")
        editor.putString(Constants.TOKEN, "")
        editor.apply()
        finish()
    }

    private fun showDialog() {

        var fragment = ChangePasswordDialog()

        var bundle = Bundle()
        bundle.putString(Constants.EMAIL, mEmail)
        bundle.putString(Constants.TOKEN, mToken)
        fragment.arguments = bundle

        fragment.show(supportFragmentManager, ChangePasswordDialog.TAG)
    }

    private fun showDialog_withdraw(){
        var fragment = WithdrawDialog()

        var bundle = Bundle()
        bundle.putString(Constants.EMAIL, mEmail)
        bundle.putString(Constants.TOKEN, mToken)
        fragment.arguments = bundle

        fragment.show(supportFragmentManager, WithdrawDialog.TAG)

    }

    private fun loadProfile() {

        mSubscriptions!!.add(NetworkUtil.getRetrofit(mToken!!).getProfile(mEmail!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }

    private fun handleResponse(user: User) {

        progress.visibility = View.GONE
        tv_name.text = user.getName()
        tv_email.text = user.getEmail()
        tv_date.text = user.getCreated_at()
    }

    private fun handleError(error: Throwable) {

        progress.visibility = View.GONE

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

    override fun onDestroy() {
        super.onDestroy()
        mSubscriptions!!.unsubscribe()
    }

    override fun onPasswordChanged() {
        showSnackBarMessage("Password Changed Successfully !");
    }

    override fun onWithdrawFinish(message: String) {
        showSnackBarMessage(message)
        Thread.sleep(2000)
    }

    private fun showSnackBarMessage(message: String) {

        Snackbar.make(findViewById(R.id.activity_profile), message, Snackbar.LENGTH_SHORT).show()

    }


}
