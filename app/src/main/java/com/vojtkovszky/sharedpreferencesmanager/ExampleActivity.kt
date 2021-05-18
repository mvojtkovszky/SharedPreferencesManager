package com.vojtkovszky.sharedpreferencesmanager

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.vojtkovszky.sharedpreferencesmanager.databinding.ActivityExampleBinding
import com.vojtkovszky.sharedpreferencesmanager.model.Dog
import kotlinx.serialization.json.Json

class ExampleActivity : AppCompatActivity() {

    companion object {
        private const val KEY_DOGGO = "KEY_DOGGO"
        private const val KEY_CHECKBOX = "KEY_CHECKBOX"
    }

    private lateinit var binding: ActivityExampleBinding
    private lateinit var preferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesManager = SharedPreferencesManager(
                context = applicationContext,
                fileKey = "myCustomFileName",
                operatingMode = Context.MODE_PRIVATE,
                json = Json { isLenient = true },
                errorListener = { it.printStackTrace() }
        )
        // preferencesManager = SharedPreferencesManager(this)
        // would work too, as other parameters are optional

        // populate doggo fields
        val doggo: Dog? = preferencesManager.getObject(KEY_DOGGO)
        binding.doggoName.setText(doggo?.name)
        binding.doggoBreed.setText(doggo?.breed)
        binding.doggoWeight.setText(doggo?.weightGrams?.toString())

        // populate random field
        binding.randomCheckbox.isChecked = preferencesManager.getBoolean(KEY_CHECKBOX, false)

        // save changes
        binding.saveButton.setOnClickListener {
            // save doggo based on edit texts
            preferencesManager.setObject(KEY_DOGGO,
                    Dog(
                            name = binding.doggoName.text.toString(),
                            breed = binding.doggoBreed.text.toString(),
                            weightGrams = binding.doggoWeight.text.toString().toIntOrNull() ?: 0
                    )
            )

            // save checkbox state
            preferencesManager.setBoolean(KEY_CHECKBOX, binding.randomCheckbox.isChecked)

            // show success to user
            Toast.makeText(this@ExampleActivity, "Preferences updated!", Toast.LENGTH_LONG).show()
        }
    }
}