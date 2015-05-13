package simonov.pk.testprojectvacancy;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by petr on 11-May-15.
 */
public class GetTranslate extends AsyncTask<String, String, String> {

    TextView yaTranslate = MainActivity.textView;

    @Override
    protected String doInBackground(String... text) {

        // Ключ для API запросов.
        String key = "trnsl.1.1.20150511T094253Z.3943295d04040c71.ebfc8f4a2e6287ca7345e191d76b7ef33b8c582c";
        // Базовая ссылка API для получения языка вводимого текста.
        String apiLinkGetLang = "https://translate.yandex.net/api/v1.5/tr.json/detect?";
        // Базовая ссылка API для получения перевода.
        String apiLinkTranslate = "https://translate.yandex.net/api/v1.5/tr.json/translate?";
        // Разделяем введенный текст на слова.
        String[] words = text[0].split(" ");
        // Создаем строку переведенного текста.
        StringBuilder translateText = new StringBuilder();
        // Переводим каждое слово.
        for (String word : words) {
            // Создаем URL, чтобы определить язык вводимого текста.
            String searchURL = apiLinkGetLang + "key=" + key + "&text=" + word;
            // Язык вводимого текста.
            String langIn = getYaBuilder(searchURL, "lang");
            // Создаем параметр языков для перевода (язык вводимого текста-язык перевода).
            String lang;
            if (MainActivity.langOut.equals("auto")) {
                if (langIn.equals("ru")) {
                    lang = langIn + "-" + "ko";
                } else {
                    lang = langIn + "-" + "ru";
                }
            } else {
                lang = langIn + "-" + MainActivity.langOut;
            }
            // Создаем URL, чтобы получить перевод.
            searchURL = apiLinkTranslate + "key=" + key + "&text=" + word + "&lang=" + lang;

            Log.e("myTag", searchURL);

            translateText.append(getYaBuilder(searchURL, "text") + " ");

        }
        // Передаем перевод в onPostExecute.
        return translateText.toString().trim();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Записываем принимаемый текст в поле для перевода.
        yaTranslate.setText(result);
        // Убираем Progress Bar.
        MainActivity.progressBar.setVisibility(View.INVISIBLE);
        // Выделяем текст перевода более чётко.
        yaTranslate.setTextColor(Color.parseColor("#000000"));

    }

    public String getYaBuilder(String searchURL, String item) {
        // Получаем ответ от сервера по запросу searchURL.
        StringBuilder yaFeedBuilder = new StringBuilder();
        HttpClient yaClient = new DefaultHttpClient();
        try {
            HttpGet yaGet = new HttpGet(searchURL);
            HttpResponse yaResponse = yaClient.execute(yaGet);
            StatusLine searchStatus = yaResponse.getStatusLine();
            if (searchStatus.getStatusCode() == 200) {
                HttpEntity yaEntity = yaResponse.getEntity();
                InputStream yaContent = yaEntity.getContent();
                InputStreamReader yaInput = new InputStreamReader(yaContent);
                BufferedReader yaReader = new BufferedReader(yaInput);
                String lineInput;
                while ((lineInput = yaReader.readLine()) != null) {
                    yaFeedBuilder.append(lineInput);
                }
            } else {
                yaFeedBuilder.append("Nothing happens");
                Log.e("myTag", searchURL);
            }
        } catch (Exception e) {
            yaFeedBuilder.append("exception");
        }
        // Создаем строку вывода.
        StringBuilder yaResult = new StringBuilder();
        // Парсим JSON объект.
        try {
            JSONObject jsonObject = new JSONObject(yaFeedBuilder.toString());
            // Получаем значение в поле code. Если code = 200, то запрос прошел успешно.
            String code = jsonObject.getString("code");
            if (code.equals("200")) {
                // Добавляем полученные данные в строку вывода.
                yaResult.append(jsonObject.getString(item));
                // Если получем значения из поля text, то убиарем лишние символы
                if (item.equals("text")) {
                    yaResult.delete(0, 2).delete(yaResult.length() - 2, yaResult.length());
                }
            }
        } catch (Exception e) {
            yaResult.append("No result!!!");
            e.printStackTrace();
        }
        // Возвращаем данные в виде строки.
        return yaResult.toString();
    }
}
