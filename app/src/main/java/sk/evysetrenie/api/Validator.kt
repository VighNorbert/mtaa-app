package sk.evysetrenie.api

import android.view.View
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class Validator {

    fun validateRequired(input: TextView, layout: TextInputLayout, inputName: String): Boolean {
        if (input.text.toString().trim().isEmpty()) {
            layout.error = "Pole '$inputName' je povinné!"
            input.requestFocus()
            return false
        }
        layout.isErrorEnabled = false
        return true
    }

    fun validateEmail(input: TextInputEditText, layout: TextInputLayout, inputName: String): Boolean {
        if (!validateRequired(input, layout, inputName)) {
            return false
        } else if (!isValidEmail(input.text.toString())) {
            layout.error = "Pole '$inputName' musí obsahovať validnú e-mailovú adresu!"
            input.requestFocus()
            return false
        }
        layout.isErrorEnabled = false
        return true
    }


    fun validatePhone(input: TextInputEditText, layout: TextInputLayout, inputName: String): Boolean {
        if (!validateRequired(input, layout, inputName)) {
            return false
        } else if (!isValidPhone(input.text.toString())) {
            layout.error = "Pole '$inputName' musí obsahovať validné telefónne číslo!"
            input.requestFocus()
            return false
        }
        layout.isErrorEnabled = false
        return true
    }

    fun validatePassword(input: TextInputEditText, layout: TextInputLayout, inputName: String): Boolean {
        if (input.text.toString().isEmpty()) {
            layout.isErrorEnabled = false
            return true
        } else if (input.text.toString().length < 8) {
            layout.error = "Pole '$inputName' musí mať dĺžku aspoň 8 znakov!"
            input.requestFocus()
            return false
        } else if (!hasNumber(input.text.toString())) {
            layout.error = "Pole '$inputName' musí obsahovať aspoň 1 číslicu!"
            input.requestFocus()
            return false
        } else if (!hasUpperAndLowerCase(input.text.toString())) {
            layout.error = "Pole '$inputName' musí obsahovať aspoň 1 veľké a 1 malé písmeno!"
            input.requestFocus()
            return false
        }
        layout.isErrorEnabled = false
        return true
    }

    fun validateNumber(input: TextInputEditText, layout: TextInputLayout, inputName: String): Boolean {
        if (!validateRequired(input, layout, inputName)) {
            return false
        } else if (!isValidNumber(input.text.toString())) {
            layout.error = "Pole '$inputName' musí obsahovať číslo!"
            input.requestFocus()
            return false
        }
        layout.isErrorEnabled = false
        return true
    }

    fun validateMaxLength(input: TextInputEditText, layout: TextInputLayout, inputName: String, maxLength: Int): Boolean {
        if (input.text.toString().trim().length > maxLength) {
            layout.error = "Pole '$inputName' musí obsahovať maximálne $maxLength znakov!"
            input.requestFocus()
            return false
        }
        layout.isErrorEnabled = false
        return true
    }

    fun validateMinLength(input: TextInputEditText, layout: TextInputLayout, inputName: String, minLength: Int): Boolean {
        if (input.text.toString().trim().length < minLength) {
            layout.error = "Pole '$inputName' musí obsahovať minimálne $minLength znakov!"
            input.requestFocus()
            return false
        }
        layout.isErrorEnabled = false
        return true
    }

    fun validateMax(input: TextInputEditText, layout: TextInputLayout, inputName: String, max: Int): Boolean {
        if (input.text.toString().toInt() > max) {
            layout.error = "Hodnota poľa '$inputName' musí byť maximálne '$max'!"
            input.requestFocus()
            return false
        }
        layout.isErrorEnabled = false
        return true
    }

    fun validateMin(input: TextInputEditText, layout: TextInputLayout, inputName: String, min: Int): Boolean {
        if (input.text.toString().toInt() < min) {
            layout.error = "Hodnota poľa '$inputName' musí byť minimálne '$min'!"
            input.requestFocus()
            return false
        }
        layout.isErrorEnabled = false
        return true
    }

    fun <T> validateArrayRequired(list: List<T>, textView: TextView): Boolean {
        if (list.isEmpty()) {
            textView.visibility = View.VISIBLE
            textView.requestFocus()
            return false
        }
        textView.visibility = View.GONE
        return true
    }

    private fun hasUpperAndLowerCase(string: String): Boolean {
        return string.contains(Regex("[A-Z]")) && string.contains(Regex("[a-z]"))
    }

    private fun hasNumber(string: String): Boolean {
        return string.contains(Regex("[0-9]"))
    }

    private fun isValidEmail(email: String): Boolean {
        return "^[A-Za-z](.*)@(.+)(\\.)(.+)".toRegex().matches(email);
    }

    private fun isValidPhone(phone: String): Boolean {
        return "^(\\+420|\\+421|0)( ?[0-9]{3}){3}\$".toRegex().matches(phone)
    }

    private fun isValidNumber(number: String): Boolean {
        return "-?[0-9]+".toRegex().matches(number)
    }
}