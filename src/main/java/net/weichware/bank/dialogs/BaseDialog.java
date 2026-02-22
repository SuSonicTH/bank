package net.weichware.bank.dialogs;

import com.vaadin.flow.component.ModalityMode;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public abstract class BaseDialog extends Dialog {
    protected VerticalLayout layout = new VerticalLayout();
    protected boolean isSmall;

    public BaseDialog(String title) {
        setHeaderTitle(title);
        getHeader().add(new Button(new Icon("lumo", "cross"), (e) -> close()));
        setModality(ModalityMode.STRICT);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        UI.getCurrent().getPage().retrieveExtendedClientDetails(evt -> {
            if (evt.getWindowInnerWidth() < 600) {
                setTop("10px");
                isSmall = true;
            }
        });


        layout.setMargin(false);
        layout.setSpacing(false);
        layout.setPadding(false);

        add(layout);
    }
}
