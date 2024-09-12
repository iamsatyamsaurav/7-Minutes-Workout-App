package com.example.a7minutesworkout

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.a7minutesworkout.databinding.ActivityExerciseBinding
import com.example.a7minutesworkout.databinding.DialogCustomBackConfirmationBinding
import kotlinx.coroutines.CoroutineScope
import java.util.Locale

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        setCustomDialogForBackButton()
    }


    private var binding: ActivityExerciseBinding?=null

    private var restTimer:CountDownTimer?=null //timer for relaxing /resting
    private var restProgress=0

    private var exerciseTimer:CountDownTimer?=null //timer for exercise
    private var exerciseProgress=0

    private var exerciseList:ArrayList<ExerciseModel>?=null
    private var currentExercisePosition=-1

    private var tts:TextToSpeech?=null   //declare tts

    private var player:MediaPlayer?=null  //declare player for media player

    private var exerciseAdapter:ExerciseStatusAdapter?=null

    private var exerciseTimerDuration:Long=1
    private var restTimerDuration:Long=1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        //toolbar
        setSupportActionBar(binding?.toolbarExercise)  //This sets up the toolbarExercise view (likely a Toolbar defined in your layout) as the app's ActionBar

        if(supportActionBar!=null){  //Checks if the supportActionBar is not null (meaning it was successfully set).
            //If the supportActionBar is not null, this line adds a back button (an arrow) to the left side of the toolbar. This button is usually used for navigating back in the app.
            supportActionBar?.setDisplayHomeAsUpEnabled(true)  //this will show the back button on the toolbar
        }


        //This sets up what happens when the user clicks the back button (the one added in the previous step)
        binding?.toolbarExercise?.setNavigationOnClickListener{
            setCustomDialogForBackButton()
        }
        //toolbar


        exerciseList= Constants.defaultExerciseList()

        tts= TextToSpeech(this,this)   //initialise tts


        setupRestView()

        setupExerciseStatusRecyclerView()

    }



    private fun setCustomDialogForBackButton(){
        val customDialog=Dialog(this)
        val dialogBinding=DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.cancel()
        }
        dialogBinding.btnNo.setOnClickListener {
            customDialog.cancel()
        }
        customDialog.show()
    }

    private fun setupExerciseStatusRecyclerView(){
        binding?.rvExerciseStatus?.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        exerciseAdapter=ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter=exerciseAdapter
    }

    //just to make sure that whenever we go back and then again move to the exercise activity, the timer restarts from beginning
    private fun setupRestView(){

        try{
            val soundURI= Uri.parse("android.resource://com.example.a7minutesworkout/"
            +R.raw.press_start)
            player=MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping=false
            player?.start()

        }catch (e:Exception){
            e.printStackTrace()
        }

        binding?.flRestView?.visibility= View.VISIBLE
        binding?.tvTitle?.visibility=View.VISIBLE
        binding?.tvExerciseName?.visibility=View.INVISIBLE
        binding?.flExerciseView?.visibility=View.INVISIBLE
        binding?.ivImage?.visibility=View.INVISIBLE

        binding?.tvUpcomingLabel?.visibility=View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility=View.VISIBLE

        if(restTimer!=null){
            restTimer!!.cancel()
            restProgress=0
        }
        binding?.tvUpcomingExerciseName?.text = exerciseList!![currentExercisePosition+1].getName()



        setRestProgressBar()
    }
    private fun setupExerciseView(){
        binding?.flRestView?.visibility= View.INVISIBLE
        binding?.tvTitle?.visibility=View.INVISIBLE
        binding?.tvExerciseName?.visibility=View.VISIBLE
        binding?.flExerciseView?.visibility=View.VISIBLE
        binding?.ivImage?.visibility=View.VISIBLE

        binding?.tvUpcomingLabel?.visibility=View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility=View.INVISIBLE

        if(exerciseTimer!=null){
            exerciseTimer!!.cancel()
            exerciseProgress=0
        }



        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()
        speakOut(binding?.tvExerciseName?.text.toString())

        setExerciseProgressBar()

    }

    private fun setRestProgressBar(){
        binding?.progressBar?.progress=restProgress

        //timer will be of 10s and the interval will be 1s
        restTimer=object :CountDownTimer(restTimerDuration*1000,1000){

            //this onTick() function will execute after every 1000 ms or 1s
            override fun onTick(p0: Long) {
                restProgress++
                binding?.progressBar?.progress=10-restProgress
                binding?.tvTimer?.text=(10-restProgress).toString()
            }

            //this onFinish() fun will execute after the timer is finished
            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()  //necessary to make changes in the recycler view as this will let the adapter know that the data has been changed based on which recycler view will be changed

               setupExerciseView()
            }

        }.start()
    }
    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress=exerciseProgress

        //timer will be of 30s and the interval will be 1s
        exerciseTimer=object :CountDownTimer(exerciseTimerDuration*1000,1000){

            //this onTick() function will execute after every 1000 ms or 1s
            override fun onTick(p0: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress=30-exerciseProgress
                binding?.tvTimerExercise?.text=(30-exerciseProgress).toString()
            }

            //this onFinish() fun will execute after the timer is finished
            override fun onFinish() {
                exerciseList!![currentExercisePosition].setIsSelected(false)
                exerciseList!![currentExercisePosition].setIsCompleted(true)
                exerciseAdapter!!.notifyDataSetChanged()
                if(currentExercisePosition<exerciseList?.size!!-1){
                    setupRestView()
                }
                else{
                    //all exercises finished
                    finish()  //remove from the stack
                    val intent=Intent(this@ExerciseActivity,FinishActivity::class.java)
                    startActivity(intent)
                }
            }

        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(restTimer!=null){
            restTimer!!.cancel()
            restProgress=0
        }
        if(exerciseTimer!=null){
            exerciseTimer!!.cancel()
            exerciseProgress=0
        }

        if(tts!=null){
            tts?.stop()
            tts?.shutdown()
        }
        if(player!=null){
            player?.stop()
        }


        binding=null
    }

    private fun speakOut(text:String){
        tts?.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }

    override fun onInit(status: Int) {
        if(status==TextToSpeech.SUCCESS) {  //if the text to speech is ready
            //set the right language for text to speech
            val result=tts!!.setLanguage(Locale.UK)
            if(result==TextToSpeech.LANG_NOT_SUPPORTED ||
                result==TextToSpeech.LANG_MISSING_DATA){
                Log.e("TTS","The Language specified is not supported")
            }


        }else{
            Log.e("TTS","Initialization Failed")
        }
    }
}