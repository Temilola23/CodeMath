package com.example.codemath

import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.codemath.databinding.ActivityMainBinding
import kotlin.math.ceil
import android.view.View
import android.view.inputmethod.InputMethodManager

class CodeMathActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tipPercentageTextView.text = getString(R.string.default_tip_percentage, binding.tipPercentageSeekBar.progress)

        binding.tipPercentageSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.tipPercentageTextView.text = getString(R.string.tip_percentage, progress)
                calculateAndUpdate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.billAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                calculateAndUpdate()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        binding.numberOfPeopleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                calculateAndUpdate()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        binding.roundingOptionsRadioGroup.setOnCheckedChangeListener { _, _ ->
            calculateAndUpdate()
        }

        // Setup the calculate button click listener
        binding.calculateButton.setOnClickListener {
            // Hide the keyboard
            hideKeyboard()

            // Perform the calculation
            calculateAndUpdate()

            // Any other action you want to take when the button is pressed
        }
    }

    private fun calculateAndUpdate() {
        val billAmount = binding.billAmountEditText.text.toString().toDoubleOrNull() ?: 0.0
        val numberOfPeople = binding.numberOfPeopleEditText.text.toString().toIntOrNull() ?: 1
        val tipPercentage = binding.tipPercentageSeekBar.progress
        var tipAmount = billAmount * tipPercentage / 100
        var totalAmount = billAmount + tipAmount

        // Apply the selected rounding rule
        when (binding.roundingOptionsRadioGroup.checkedRadioButtonId) {
            R.id.roundTipRadioButton -> tipAmount = ceil(tipAmount)
            R.id.roundTotalRadioButton -> totalAmount = ceil(totalAmount)
        }

        // Calculate the amount per person
        val amountPerPerson = totalAmount / numberOfPeople

        // Update the UI
        binding.tipAmountTextView.text = getString(R.string.tip_amount, tipAmount)
        binding.totalAmountTextView.text = getString(R.string.total_amount, totalAmount)
        if (numberOfPeople > 1) {
            binding.amountPerPersonTextView.visibility = View.VISIBLE
            binding.amountPerPersonTextView.text = getString(R.string.amount_per_person, amountPerPerson)
        } else {
            binding.amountPerPersonTextView.visibility = View.GONE
        }
    }

    // Function to hide the keyboard
    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}
