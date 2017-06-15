package com.nakhmedov.finance.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.constants.ContextConstants;
import com.nakhmedov.finance.db.entity.Country;
import com.nakhmedov.finance.net.FinanceHttpService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/21/17
 * Time: 11:21 PM
 * To change this template use File | Settings | File Templates
 */

public class CalculatorFragment extends BaseFragment {

    private static final String TAG = CalculatorFragment.class.getCanonicalName();

    @BindView(R.id.content_view) View contentView;
    @BindView(R.id.from_country) AppCompatSpinner fromSpinner;
    @BindView(R.id.to_country) AppCompatSpinner toSpinner;
    @BindView(R.id.amount_view) TextInputLayout amountInputView;
    @BindView(R.id.amount_edit_view) TextInputEditText amountView;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.reslut_view) View resultView;
    @BindView(R.id.sum_value) TextView sumView;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_calculator;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] isoCountries = Locale.getISOCountries();
        List<Country> countries = new ArrayList<>(isoCountries.length);
        for(int i = 0; i < isoCountries.length; i++){
            Locale currencyLocal = new Locale("", isoCountries[i]);
            String countryName = currencyLocal.getDisplayCountry();
            try {
                Currency currency = Currency.getInstance(currencyLocal);
                if (currency != null) {
                    String currencyName = currency.getCurrencyCode();
                    if (countryName.length() > 0 && !countries.contains(countryName)) {
                        Country country = new Country(countryName, currencyName);
                        countries.add(country);
                    }
                }

            } catch (IllegalArgumentException exception) {
                    exception.printStackTrace();
            }
        }
        Collections.sort(countries, new Comparator<Country>() {
            @Override
            public int compare(Country o1, Country o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        int fromSpinnerPosition = 0, toSpinnerPosition = 0;
        for (int i = 0; i < countries.size(); i++) {
            Country country = countries.get(i);
            if (country.getName().equalsIgnoreCase("United States")) {
                fromSpinnerPosition = i;
            } else if (country.getName().equalsIgnoreCase("Uzbekistan")) {
                toSpinnerPosition = i;
            }
            if (fromSpinnerPosition > 0 && toSpinnerPosition > 0) {
                break;
            }
        }
        ArrayAdapter<Country> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, countries);
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);

        fromSpinner.setSelection(fromSpinnerPosition);
        toSpinner.setSelection(toSpinnerPosition);


        amountView.setOnEditorActionListener(new TextInputEditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doCalculator(amountView.getText().toString(), ((Country) fromSpinner.getSelectedItem()).getCurrency(),
                            ((Country) toSpinner.getSelectedItem()).getCurrency());
                    return true;
                }
                return false;
            }
        });

        amountView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    amountInputView.setError(null);
                    hideResultView();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @OnClick(R.id.fab)
    public void onConvertClicked() {
        doCalculator(amountView.getText().toString(), ((Country) fromSpinner.getSelectedItem()).getCurrency(),
                ((Country) toSpinner.getSelectedItem()).getCurrency());
    }
    private void doCalculator(final String amount, String fromCurrency, final String toCurrency) {
        if (amount.isEmpty()) {
            amountInputView.setError(getString(R.string.need_enter_value));
            return;
        }
        showLoading();
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(interceptor)
//                .build();
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
        Retrofit retrofit = new Retrofit.Builder()
//                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(ContextConstants.CONVERTER_URL)
                .build();

        FinanceHttpService service = retrofit.create(FinanceHttpService.class);
        service.getConvertRate(amount, fromCurrency, toCurrency).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        String regex = "<span class=bld>(.+?)</span>";
                        final Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(responseBody);
                        if (matcher.find()) {
                            String resultString = matcher.group(1);
                            showResult(resultString);
                        } else {
                            showMessage(getString(R.string.smth_wrong));
                        }

                    } catch (Exception e) {
                        showMessage(getString(R.string.smth_wrong));
                        e.printStackTrace();
                    }
                }
                hideLoading();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                throwable.printStackTrace();
                showMessage(getString(R.string.check_net));
                hideLoading();
            }
        });
    }

    private void showResult(String result) {
//        DecimalFormat df = new DecimalFormat("#,###.##");

//        String result = df.format(rate*amount);
        sumView.setText(" " + result);
        showResultView();
    }

    private void showResultView() {
        resultView.setVisibility(View.VISIBLE);
    }

    private void hideResultView() {
        resultView.setVisibility(View.GONE);
    }

    private void showMessage(String text) {
        Snackbar.make(contentView, text, Snackbar.LENGTH_SHORT).show();
    }
}
