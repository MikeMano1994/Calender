package cog.com.kotlin_google_plus

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.plus.Plus

class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private var mGoogleApiClient: GoogleApiClient? = null

    private var mIntentInProgress: Boolean = false

    private var mSignInClicked: Boolean = false

    private var GooglePlusFlag = false

    private var mConnectionResult: ConnectionResult? = null

    private var WEB_CLIENT_ID = "AIzaSyAxlJC3hUaH4RrdRM45lyut1l5VF3VZaAs"


    lateinit var google_login: Button


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        //************ Google+ *************


        /*mGoogleApiClient = GoogleApiClient.Builder(this@MainActivity)

                .addConnectionCallbacks(this@MainActivity)

                .addOnConnectionFailedListener(this).addApi<Plus.PlusOptions>(Plus.API, Plus.PlusOptions.builder().build())

                .addScope(Plus.SCOPE_PLUS_LOGIN).build()*/

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(WEB_CLIENT_ID)
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this@MainActivity)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()



        google_login = findViewById(R.id.google_login) as Button

        google_login.setOnClickListener {

            /*GooglePlusFlag = true

            SignUpGmail()*/
            startActivity(Intent(applicationContext,MapsActivity::class.java))

        }

    }


    //************** Google+ Start******************


    private fun SignUpGmail() {

        println("SignUpGmailSignUpGmailSignUpGmail 1")

       /* if (cog.com.kotlin_google_plus.NetWorkStatus.isNetworkAvailable(this@MainActivity)) {*/


            if (mGoogleApiClient!!.isConnected) {
                println("SignUpGmailSignUpGmailSignUpGmail 2")

                getProfileInformation()

            } else {
                println("SignUpGmailSignUpGmailSignUpGmail 3")

                signInWithGplus()

            }


        /*} else {

            Toast.makeText(this@MainActivity, "Network_not_availabl", Toast.LENGTH_SHORT).show()

        }*/


    }


    override fun onStart() {

        super.onStart()

        mGoogleApiClient!!.connect()

    }


    override fun onStop() {

        super.onStop()

        if (mGoogleApiClient!!.isConnected) {

            mGoogleApiClient!!.disconnect()

        }

    }


    /**

     * Method to resolve any signin errors

     */

    private fun resolveSignInError() {

        if (mConnectionResult!!.hasResolution()) {
            println("SignUpGmailSignUpGmailSignUpGmail 5")

            try {

                mIntentInProgress = true

                //mConnectionResult!!.startResolutionForResult(this, RC_SIGN_IN)

            } catch (e: Exception) {

                mIntentInProgress = false

                mGoogleApiClient!!.connect()

            }


        }

    }


    override fun onConnectionFailed(result: ConnectionResult) {

        if (!result.hasResolution()) {

            GooglePlayServicesUtil.getErrorDialog(result.errorCode, this,

                    0).show()

            return

        }


        if (!mIntentInProgress) {

            // Store the ConnectionResult for later usage

            mConnectionResult = result


            if (mSignInClicked) {

                // The user has already clicked 'sign-in' so we attempt to

                // resolve all

                // errors until the user is signed in, or they cancel.

                resolveSignInError()

            }

        }


    }


    override fun onConnected(arg0: Bundle?) {

        mSignInClicked = false

        //Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();


        // Get user's information

        getProfileInformation()


        // Update the UI after signin

        updateUI(true)


    }


    /**

     * Updating the UI, showing/hiding buttons and profile layout

     */

    private fun updateUI(isSignedIn: Boolean) {


    }


    /**

     * Fetching user's information name, email, profile pic

     */

    @SuppressLint("MissingPermission")
    private fun getProfileInformation() {

        if (GooglePlusFlag) {

            try {

                if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {

                    val currentPerson = Plus.PeopleApi

                            .getCurrentPerson(mGoogleApiClient)


                    println("currentPerson***********" + currentPerson)


                    var SgID = ""

                    var Sname = ""

                    var Semail = ""

                    var Surl = ""

                    val Sdob = ""

                    val Sgender = ""


                    val personGooglePlusProfile = currentPerson.url


                    SgID = currentPerson.id


                    Sname = currentPerson.displayName


                    Semail = Plus.AccountApi.getAccountName(mGoogleApiClient)


                    Surl = currentPerson.image.url

                    println("gmail details $SgID $Sname $Semail $Surl")


                } else {

                    Toast.makeText(applicationContext,

                            "Person information not found", Toast.LENGTH_LONG).show()

                }

            } catch (e: Exception) {

                e.printStackTrace()

            }


        }

        GooglePlusFlag = false

    }


    override fun onConnectionSuspended(arg0: Int) {

        mGoogleApiClient!!.connect()

        updateUI(false)

    }


    /**

     * Sign-in into google

     */

    private fun signInWithGplus() {

        println("SignUpGmailSignUpGmailSignUpGmail 4")

        if (!mGoogleApiClient!!.isConnecting) {

            mSignInClicked = true

            resolveSignInError()

        }

    }


    /**

     * Sign-out from google

     */

    private fun signOutFromGplus() {

        if (mGoogleApiClient!!.isConnected) {

            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient)

            mGoogleApiClient!!.disconnect()

            mGoogleApiClient!!.connect()

            updateUI(false)

        }

    }
}

