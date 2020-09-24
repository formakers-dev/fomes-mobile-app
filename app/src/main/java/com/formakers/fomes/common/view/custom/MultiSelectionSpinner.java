package com.formakers.fomes.common.view.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectionSpinner extends AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener {

    List<String> items;
    boolean[] selections;
    ArrayAdapter adapter;

    public MultiSelectionSpinner(Context context) {
        super(context);
        adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item);
        super.setAdapter(adapter);
    }

    public MultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item);
        super.setAdapter(adapter);
    }

    private void displayTitle() {
        String selectedItemString = Stream.of(getSelectedItems()).collect(Collectors.joining(","));
        String displayText = (selectedItemString != null && selectedItemString.length() > 0) ? selectedItemString : getPrompt().toString();

        adapter.clear();
        adapter.add(displayText);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (selections != null && which < selections.length) {
            selections[which] = isChecked;

            displayTitle();
        } else {
            throw new IllegalArgumentException("Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getPrompt());
        builder.setMultiChoiceItems(items.toArray(new String[items.size()]), selections, this);

        builder.setPositiveButton("OK", (arg0, arg1) -> {
            //Do nothing
        });

        builder.setOnDismissListener((arg0) -> {
            getOnItemSelectedListener().onItemSelected(this, this, 0, 0);
        });

        builder.show();
        return true;
    }


    public void setItems(List<String> items) {
        this.items = items;
        this.selections = new boolean[items.size()];
        displayTitle();
    }

    public void setSelections(List<String> selectedItems) {
        for (String selectedItem : selectedItems) {
            int index = items.indexOf(selectedItem);

            if (index >= 0) {
                this.selections[index] = true;
            }
        }

        displayTitle();
    }

    public List<String> getSelectedItems() {
        List<String> selectedItems = new ArrayList();

        for (int i = 0; i < items.size(); i++) {
            if (selections[i]) {
                selectedItems.add(items.get(i));
            }
        }

        return selectedItems;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException("setAdapter is not supported by MultiSelectSpinner.");
    }
}
