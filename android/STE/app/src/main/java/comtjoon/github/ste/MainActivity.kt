package comtjoon.github.ste

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import comtjoon.github.ste.fragments.LoginFragment
import comtjoon.github.ste.fragments.ResetPasswordDialog


class MainActivity : AppCompatActivity(), ResetPasswordDialog.Listener {


    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    private var mLoginFragment: LoginFragment? = null
    private var mResetPasswordDialog: ResetPasswordDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            loadFragment()
        }
    }

    private fun loadFragment() {

        if (mLoginFragment == null) {
            mLoginFragment = LoginFragment()
        }
        supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame, mLoginFragment, LoginFragment.TAG).commit()
    }

    /*override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val data = intent!!.getData().lastPathSegment
        Log.d(TAG, "onNewIntent: $data")

        mResetPasswordDialog = fragmentManager.findFragmentByTag(ResetPasswordDialog.TAG) as ResetPasswordDialog

        if (mResetPasswordDialog != null)
            mResetPasswordDialog!!.setToken(data)
    }*/

    override fun onPasswordReset(message: String) {
        showSnackBarMessage(message)
    }

    private fun showSnackBarMessage(message: String) {

        Snackbar.make(findViewById<View>(R.id.activity_main), message, Snackbar.LENGTH_SHORT).show()

    }


    /*override fun onResume() {
        super.onResume()
        loadFragment()
    }*/
}