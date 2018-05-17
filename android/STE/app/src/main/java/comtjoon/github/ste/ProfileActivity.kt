package comtjoon.github.ste

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_profile.*
import rx.subscriptions.CompositeSubscription
import android.content.SharedPreferences
import comtjoon.github.ste.utils.Constants


class ProfileActivity : AppCompatActivity() {
    companion object {
        var TAG = ProfileActivity::class.java.simpleName
    }

    private var mSubscriptions: CompositeSubscription? = null
    private val mSharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        mSubscriptions = CompositeSubscription()

    }

    private fun initViews() {
        btn_change_password.setOnClickListener {
            showDialog()
        }
        btn_logout.setOnClickListener {
            logout()
        }
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
        fragment.setArguments(bundle)

        fragment.show(fragmentManager, ChangePasswordDialog.TAG)
    }
}
