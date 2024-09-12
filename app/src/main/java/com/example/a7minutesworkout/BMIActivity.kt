package com.example.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.a7minutesworkout.databinding.ActivityBmiactivityBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {
    companion object {
        private const val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW" // Metric Unit View
        private const val US_UNITS_VIEW = "US_UNIT_VIEW" // US Unit View
    }

    private var currentVisibleView: String = METRIC_UNITS_VIEW // A variable to hold a value to make a selected view visible

    private var binding:ActivityBmiactivityBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBmiactivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarBmi)  //This sets up the toolbarExercise view (likely a Toolbar defined in your layout) as the app's ActionBar

        if(supportActionBar!=null){  //Checks if the supportActionBar is not null (meaning it was successfully set).
            //If the supportActionBar is not null, this line adds a back button (an arrow) to the left side of the toolbar. This button is usually used for navigating back in the app.
            supportActionBar?.setDisplayHomeAsUpEnabled(true)  //this will show the back button on the toolbar
            supportActionBar!!.title="BMI Calculator"
        }


        //This sets up what happens when the user clicks the back button (the one added in the previous step)
        binding?.toolbarBmi?.setNavigationOnClickListener{
            onBackPressed()
        }

        binding?.btnCalculateUnits?.setOnClickListener {
            calculateBMI()
        }

        binding?.rgUnits?.setOnCheckedChangeListener{_,checkedId:Int->
            if(checkedId==R.id.rbMetricUnits){
                makeVisibleMetricUnitsView()
            }
            else{
                makeVisibleUSUnitsView()
            }
        }



    }

    private fun makeVisibleMetricUnitsView(){
        currentVisibleView= METRIC_UNITS_VIEW
        binding?.tilMetricUnitHeight?.visibility=View.VISIBLE
        binding?.tilMetricUnitWeight?.visibility=View.VISIBLE
        binding?.tilUSMetricUnitFeet?.visibility=View.INVISIBLE
        binding?.tilUSMetricUnitInch?.visibility=View.INVISIBLE
        binding?.tilUSUnitWeightPounds?.visibility=View.INVISIBLE

        binding?.etMetricUnitWeight?.text!!.clear()
        binding?.etMetricUnitHeight?.text!!.clear()
        binding?.etUSUnitFeet?.text!!.clear()
        binding?.etUSUnitInch?.text!!.clear()
        binding?.etUSUnitWeightPounds?.text!!.clear()

        binding?.llDiplayBMIResult?.visibility=View.INVISIBLE
    }
    private fun makeVisibleUSUnitsView(){
        currentVisibleView= US_UNITS_VIEW
        binding?.tilMetricUnitHeight?.visibility=View.INVISIBLE
        binding?.tilMetricUnitWeight?.visibility=View.INVISIBLE
        binding?.tilUSMetricUnitFeet?.visibility=View.VISIBLE
        binding?.tilUSMetricUnitInch?.visibility=View.VISIBLE
        binding?.tilUSUnitWeightPounds?.visibility=View.VISIBLE

        binding?.etMetricUnitWeight?.text!!.clear()
        binding?.etUSUnitFeet?.text!!.clear()
        binding?.etUSUnitInch?.text!!.clear()
        binding?.etMetricUnitHeight?.text!!.clear()
        binding?.etUSUnitWeightPounds?.text!!.clear()

        binding?.llDiplayBMIResult?.visibility=View.INVISIBLE
    }

    private fun calculateBMI(){
        if(currentVisibleView== METRIC_UNITS_VIEW){
            if(validateMetricUnits()){
                val heightValue:Float=binding?.etMetricUnitHeight?.text?.toString()?.toFloat()!!/100  //to convert to meters
                val weightValue:Float=binding?.etMetricUnitWeight?.text?.toString()?.toFloat()!!
                val bmi=weightValue/(heightValue*heightValue)
                displayBMI(bmi)
            }else{
                Toast.makeText(this,"Please enter valid values",Toast.LENGTH_LONG).show()
            }
        }
        else{
            if(validateUSUnits()){

                val usUnitHeightValueFeet: String =
                    binding?.etUSUnitFeet?.text.toString() // Height Feet value entered in EditText component.
                val usUnitHeightValueInch: String =
                    binding?.etUSUnitInch?.text.toString() // Height Inch value entered in EditText component.
                val usUnitWeightValue: Float = binding?.etUSUnitWeightPounds?.text.toString()
                    .toFloat() // Weight value entered in EditText component.

                // Here the Height Feet and Inch values are merged and multiplied by 12 for converting it to inches.
                val heightValue =
                    usUnitHeightValueInch.toFloat() + usUnitHeightValueFeet.toFloat() * 12

                // This is the Formula for US UNITS result.
                // Reference Link : https://www.cdc.gov/healthyweight/assessing/bmi/childrens_bmi/childrens_bmi_formula.html
                val bmi = 703 * (usUnitWeightValue / (heightValue * heightValue))

                displayBMI(bmi) // Displaying the result into UI

            }else{
                Toast.makeText(this,"Please enter valid values",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun displayBMI(bmi:Float){
        val bmiLabel: String
        val bmiDescription: String

        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0
        ) {
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops!You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0
        ) {
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0
        ) {
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        } else if (bmi.compareTo(25f) > 0 && bmi.compareTo(30f) <= 0
        ) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0
        ) {
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0
        ) {
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        } else {
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }
        binding?.llDiplayBMIResult?.visibility=View.VISIBLE
        val bmiValue=BigDecimal(bmi.toDouble()).setScale(2,RoundingMode.HALF_EVEN).toString()
        binding?.tvBMIValue?.text=bmiValue
        binding?.tvBMIType?.text=bmiLabel
        binding?.tvBMIDescription?.text=bmiDescription

    }

    private fun validateMetricUnits():Boolean{
        var isValild=true
        if(binding?.etMetricUnitHeight?.text?.toString()!!.isEmpty()){
            isValild = false
        }
        else if(binding?.etMetricUnitWeight?.text?.toString()!!.isEmpty()){
            isValild=false
        }
        return isValild
    }

    private fun validateUSUnits():Boolean{
        var isValild=true
        if(binding?.etUSUnitFeet?.text?.toString()!!.isEmpty()){
            isValild = false
        }
        else if(binding?.etUSUnitInch?.text?.toString()!!.isEmpty()){
            isValild=false
        }
        else if(binding?.etUSUnitWeightPounds?.text?.toString()!!.isEmpty()){
            isValild=false
        }
        return isValild
    }


}