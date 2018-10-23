package com.jskaleel.sangaelakkiyangal.ui.activities

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.jskaleel.sangaelakkiyangal.R
import com.jskaleel.sangaelakkiyangal.model.SEAppUtil
import com.jskaleel.sangaelakkiyangal.ui.BaseActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {
    var disposable: Disposable? = null
    var retryCount = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_splash)

        callCategoryApi()
    }

    private fun callCategoryApi() {
        avlProgress.visibility = View.VISIBLE
        txtError.visibility = View.INVISIBLE

        val categoryCall = SEAppUtil.getRetrofit().getCategories()
        disposable = categoryCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    SEAppUtil.setCategories(result)
                    launchNextScreen()
                }) { _ ->
                    run {
                        txtError.visibility = View.VISIBLE
                        if (retryCount < 3) {
                            txtError.text = getString(R.string.tap_to_retry)
                            txtError.setOnClickListener {
                                callCategoryApi()
                                retryCount++
                            }
                        } else {
                            txtError.text = getString(R.string.try_again_later)
                            txtError.setOnClickListener(null)
                        }
                        avlProgress.visibility = View.GONE
                    }
                }
    }

    private fun launchNextScreen() {
        startActivity(HomeActivity.getHomeIntent(this))
        this.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}