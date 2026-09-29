package com.sanad.app600245

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sanad.app600245.databinding.ActivityMainBinding
import net.objecthunter.exp4j.ExpressionBuilder
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentExpression = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        val numberButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9, binding.btnDot
        )

        val numberValues = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".")
        for (i in numberButtons.indices) {
            numberButtons[i].setOnClickListener {
                appendExpression(numberValues[i], false)
            }
        }

        binding.btnPlus.setOnClickListener { appendExpression("+", false) }
        binding.btnSubtract.setOnClickListener { appendExpression("-", false) }
        binding.btnMultiply.setOnClickListener { appendExpression("×", false) }
        binding.btnDivide.setOnClickListener { appendExpression("÷", false) }
        binding.btnBracketLeft.setOnClickListener { appendExpression("(", false) }
        binding.btnBracketRight.setOnClickListener { appendExpression(")", false) }

        binding.btnSin.setOnClickListener { appendExpression("sin(", true) }
        binding.btnCos.setOnClickListener { appendExpression("cos(", true) }
        binding.btnTan.setOnClickListener { appendExpression("tan(", true) }
        binding.btnSqrt.setOnClickListener { appendExpression("sqrt(", true) }

        binding.btnAc.setOnClickListener {
            currentExpression = ""
            binding.tvHistory.text = ""
            binding.tvResult.text = "0"
        }

        binding.btnEquals.setOnClickListener {
            calculateResult()
        }
    }

    private fun appendExpression(string: String, canBeStart: Boolean) {
        if (binding.tvResult.text.toString().startsWith("خطأ")) {
            currentExpression = ""
        }
        
        currentExpression += string
        binding.tvHistory.text = currentExpression
        
        try {
            val result = evaluateExpression(currentExpression)
            if (!result.isNaN()) {
                val df = DecimalFormat("#.########")
                binding.tvResult.text = df.format(result)
            }
        } catch (e: Exception) {
            // تجاهل الأخطاء المؤقتة أثناء الكتابة
        }
    }

    private fun calculateResult() {
        if (currentExpression.isEmpty()) return
        try {
            val result = evaluateExpression(currentExpression)
            if (result.isNaN()) {
                binding.tvResult.text = "خطأ في التعبير"
            } else {
                val df = DecimalFormat("#.########")
                binding.tvResult.text = df.format(result)
                currentExpression = df.format(result)
            }
        } catch (e: Exception) {
            binding.tvResult.text = "خطأ"
        }
    }

    private fun evaluateExpression(expression: String): Double {
        var formattedExp = expression
            .replace("×", "*")
            .replace("÷", "/")

        // إغلاق الأقواس تلقائياً إن وجدت ناقصة
        val openBrackets = formattedExp.count { it == '(' }
        val closeBrackets = formattedExp.count { it == ')' }
        for (i in 0 until (openBrackets - closeBrackets)) {
            formattedExp += ")"
        }

        val e = ExpressionBuilder(formattedExp).build()
        return e.evaluate()
    }
}