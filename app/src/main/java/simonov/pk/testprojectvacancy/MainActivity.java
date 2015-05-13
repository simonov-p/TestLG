package simonov.pk.testprojectvacancy;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by petr on 11-May-15.
 * Test Translate app for LG Electronics Inc. vacancy
 */

public class MainActivity extends ActionBarActivity {

    static TextView textView;
    static String langOut;
    static ProgressBar progressBar;
    EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Определяем View по id.
        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Создаем spinner объект.
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.languages,
                R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Присваиваем адаптер.
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        // Ставим на spinner слушатель и отслеживаем изменения язка, на который переводим.
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Передаем в переменную язык, на который переводим.
                langOut = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void onClick(View view) {
        // При нажатии на кнопку запускаем прогресс бар.
        progressBar.setVisibility(View.VISIBLE);
        // Забираем текст из поля ввода. используем trim для обрезки лишних пробелов в краях.
        String text = editText.getText().toString().trim();

        // Если было что-то введено в поле editText, выполняем попытку перевода.
        if (text.length() > 0) {
            GetTranslate getTranslate = new GetTranslate();
            getTranslate.execute(text);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // При восстановлении Activity (например: повороте экрана) получим прежнее значение в поле с
        // с переводом.
        textView.setText(savedInstanceState.get("text").toString());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Запомним значение в поле с переводом.
        outState.putString("text", textView.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
