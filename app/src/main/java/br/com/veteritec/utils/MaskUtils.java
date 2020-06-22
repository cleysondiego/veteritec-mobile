package br.com.veteritec.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class MaskUtils {

    public static final String FORMAT_CPF = "###.###.###-##";
    public static final String FORMAT_TELEPHONE = "(##)####-####";
    public static final String FORMAT_CELLPHONE = "(##)#####-####";
    public static final String FORMAT_CEP = "#####-###";

    public static TextWatcher mask(final EditText edt, final String msk) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            @Override
            public void afterTextChanged(final Editable s) {}

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {}

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                final String str = MaskUtils.unmask(s.toString());
                String mask = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (final char m : msk.toCharArray()) {
                    if (m != '#' && str.length() > old.length()) {
                        mask += m;
                        continue;
                    }
                    try {
                        mask += str.charAt(i);
                    } catch (final Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                edt.setText(mask);
                edt.setSelection(mask.length());
            }
        };
    }

    public static String unmask(final String s) {
        return s.replaceAll("[.]", "").replaceAll("[-]", "").replaceAll("[/]", "").replaceAll("[(]", "").replaceAll("[ ]","").replaceAll("[:]", "").replaceAll("[)]", "");
    }
}