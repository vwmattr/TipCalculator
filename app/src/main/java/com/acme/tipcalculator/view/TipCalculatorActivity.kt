package com.acme.tipcalculator.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.acme.tipcalculator.R
import com.acme.tipcalculator.databinding.ActivityTipCalculatorBinding
import com.acme.tipcalculator.model.TipCalculation
import com.acme.tipcalculator.viewmodel.CalculatorViewModel

class TipCalculatorActivity : AppCompatActivity(),
        LoadDialogFragment.Callback,
        SaveDialogFragment.Callback {

    /**
     *  Lab Step 1: After turning on databinding in your gradle file, and wrapping the layouts
     *  inside of `activity_tip_calculator.xml` in layout tags.  Uncomment the following
     *  lateinit var called binding of the generated binding type from `activity_tip_calculator.xml`
     **/
     lateinit var binding: ActivityTipCalculatorBinding

    private lateinit var calculatorViewModel: CalculatorViewModel

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_tip_calculator, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_save -> {
                showSaveDialog()
                true
            }
            R.id.action_load -> {
                showLoadDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        calculatorViewModel = CalculatorViewModel()

        /**
         * Lab Step 1: Use the static DataBindingUtil.setContentView function to set the content view
         * and generate the binding for this view in one step.
         *
         * Use this to set the lateinit binding var that you defined at the class level.
         */
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tip_calculator)

        setSupportActionBar(getToolbar())

        getFloatingActionButton().setOnClickListener { _ ->

            calculatorViewModel.checkAmtInput = getInputCheckoutAmountValue()
            calculatorViewModel.tipPctInput = getInputTipPercentageValue()

            // Invoke Calculate Tip on the ViewModel
            calculatorViewModel.calculateTip()

            // After the calculation, we need to manually update our view elements
            updateView()
        }
    }

    fun showSaveDialog() {
        val saveFragment = SaveDialogFragment()
        saveFragment.show(supportFragmentManager, "SaveDialog")
    }

    fun showLoadDialog() {
        val loadFragment = LoadDialogFragment()
        loadFragment.show(supportFragmentManager, "LoadDialog")
    }

    override fun onTipSelected(tipCalc: TipCalculation) {
        calculatorViewModel.loadTipCalculation(tipCalc)
        updateView(true)
        Snackbar.make(getRootView(), "Loaded ${tipCalc.locationName}", Snackbar.LENGTH_SHORT).show()
    }

    override fun onSaveTip(name: String) {
        calculatorViewModel.saveCurrentTip(name)
        updateView()
        Snackbar.make(getRootView(), "Saved $name", Snackbar.LENGTH_SHORT).show()
    }

    /**
     * Lab Step 1: Get views from the binding instead of using findViewById.
     */
    private fun getInputTipPercentageValue() : String {
        return binding.content?.inputTipPercentage?.text.toString()
    }

    private fun getInputCheckoutAmountValue() : String {
        return binding.content?.inputCheckAmount?.text.toString()
    }

    private fun getFloatingActionButton() : FloatingActionButton {
        return binding.calculateFab
    }

    private fun getRootView(): View {
        return binding.root
    }

    private fun TipCalculatorActivity.getToolbar(): Toolbar {
        return binding.toolbar
    }

    private fun updateView(inputsToo: Boolean = false) {

        if(inputsToo) {
            binding.content?.inputCheckAmount?.setText(calculatorViewModel.checkAmtInput)
            binding.content?.inputTipPercentage?.setText(calculatorViewModel.tipPctInput)
        }

        calculatorViewModel.tipCalculation.let { tc ->
            binding.content?.billAmount?.setText(getString(R.string.dollar_amount, tc.checkAmount))
            binding.content?.tipDollarAmount?.setText(getString(R.string.dollar_amount, tc.tipAmount))
            binding.content?.totalDollarAmount?.setText(getString(R.string.dollar_amount, tc.grandTotal))
            binding.content?.calculationName?.setText(tc.locationName)
        }

    }

}

