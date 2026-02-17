package net.weichware.bank;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.PrettyPrintTreeKt;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import net.weichware.bank.base.Session;
import net.weichware.bank.base.State;
import net.weichware.bank.base.StateConfig;
import net.weichware.bank.database.Database;
import net.weichware.bank.database.entities.User;
import net.weichware.bank.views.AccountView;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.mvysny.kaributesting.v10.LocatorJ._find;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UITest {
    private static Routes routes;

    @BeforeAll
    public static void createRoutes() {
        routes = new Routes().autoDiscoverViews("net.weichware.bank");
    }

    private static @NonNull String getCardText(Component component) {
        Card card = (Card) component;
        return card.getTitleAsText() + ": " + card.getHeaderSuffix().getElement().getText();
    }

    @BeforeEach
    public void setupVaadin(TestInfo testInfo) throws SQLException {
        State.init(new TestStateConfig(testInfo.getDisplayName()));
        Database.init(State.dataSource());
        MockVaadin.setup(routes);
    }

    @AfterEach
    public void teardownVaadin() {
        MockVaadin.tearDown();
    }

    @Test
    public void accountViewRedirectsIfNotLoggedIn() {
        UI.getCurrent().navigate(AccountView.class);
        //_assertOne(LoginView.class);
    }

    @Test
    public void loginTest() {
        //UI.getCurrent().navigate(LoginView.class);
        //_assertOne(LoginView.class);
        //LoginOverlay overlay = _get(LoginOverlay.class);
        //_get(overlay,Text.class,sel->sel.withLabel("Username")).setText("mike");
        //_get(overlay,Text.class,sel->sel.withLabel("Password")).setText("test");
        //_click(_get(overlay,Button.class, spec -> spec.withText("Log in")));
    }

    @Test
    public void testAdminView() {
        Session.get().setUser(new User("mike", "", "", true));
        UI.getCurrent().navigate(AccountView.class);

        var accounts = _get(VerticalLayout.class, spec -> spec.withId("accountLayout"));

        var expected = "Christoph: 0,00 €\n" +
                "Katharina: 1,96 €\n" +
                "Maximilian: -20,00 €";

        assertEquals(expected, accounts.getChildren().map(UITest::getCardText).collect(Collectors.joining("\n")));
    }

    @Test
    public void testAdminViewLayoutAndValues() {
        Session.get().setUser(new User("mike", "", "", true));
        UI.getCurrent().navigate(AccountView.class);

        List<VerticalLayout> verticalLayouts = _find(VerticalLayout.class);
        assertEquals("""
                └── VerticalLayout[#accountLayout, @style='width:100%;max-width:500px', @theme='padding spacing']
                    ├── Card[@style='width:100%']
                    │   ├── Avatar[@slot='header-prefix']
                    │   └── Span[text='0,00 €', @slot='header-suffix', @theme='badge success']
                    ├── Card[@style='width:100%']
                    │   ├── Avatar[@slot='header-prefix']
                    │   └── Span[text='1,96 €', @slot='header-suffix', @theme='badge success']
                    └── Card[@style='width:100%']
                        ├── Avatar[@slot='header-prefix']
                        └── Span[text='-20,00 €', @slot='header-suffix', @theme='badge error']
                """, PrettyPrintTreeKt.toPrettyTree(verticalLayouts.get(1)));

        assertEquals("""
                └── VerticalLayout[#transactionLayout, @style='width:100%;max-width:500px', @theme='padding spacing']
                    ├── Card[@style='width:100%']
                    │   ├── Span[text='29/01/2026', @slot='subtitle']
                    │   ├── Avatar[@slot='header-prefix']
                    │   └── Span[text='-10,00 €', @slot='header-suffix', @theme='badge error']
                    ├── Card[@style='width:100%']
                    │   ├── Span[text='29/01/2026', @slot='subtitle']
                    │   ├── Avatar[@slot='header-prefix']
                    │   └── Span[text='26,65 €', @slot='header-suffix', @theme='badge success']
                    ├── Card[@style='width:100%']
                    │   ├── Span[text='12/12/2025', @slot='subtitle']
                    │   ├── Avatar[@slot='header-prefix']
                    │   └── Span[text='20,54 €', @slot='header-suffix', @theme='badge success']
                    ├── Card[@style='width:100%']
                    │   ├── Span[text='18/12/2025', @slot='subtitle']
                    │   ├── Avatar[@slot='header-prefix']
                    │   └── Span[text='4,53 €', @slot='header-suffix', @theme='badge success']
                    ├── Card[@style='width:100%']
                    │   ├── Span[text='15/12/2025', @slot='subtitle']
                    │   ├── Avatar[@slot='header-prefix']
                    │   └── Span[text='54,70 €', @slot='header-suffix', @theme='badge success']
                    ├── Card[@style='width:100%']
                    │   ├── Span[text='15/12/2025', @slot='subtitle']
                    │   ├── Avatar[@slot='header-prefix']
                    │   └── Span[text='-50,00 €', @slot='header-suffix', @theme='badge error']
                    └── Card[@style='width:100%']
                        ├── Span[text='10/12/2025', @slot='subtitle']
                        ├── Avatar[@slot='header-prefix']
                        └── Span[text='25,00 €', @slot='header-suffix', @theme='badge success']
                """, PrettyPrintTreeKt.toPrettyTree(verticalLayouts.get(2)));
    }

    @Test
    public void testUserAccountViewLayoutAndValues() {
        Session.get().setUser(new User("maximilian", "", "", false));
        UI.getCurrent().navigate(AccountView.class);

        List<VerticalLayout> verticalLayouts = _find(VerticalLayout.class);

        assertEquals("""
                └── VerticalLayout[@style='width:100%;max-width:500px', @theme='padding spacing']
                    └── Card[@style='width:100%', @theme='outlined']
                        ├── Avatar[@slot='header-prefix']
                        └── Span[text='-20,00 €', @slot='header-suffix', @theme='badge error']
                """, PrettyPrintTreeKt.toPrettyTree(verticalLayouts.get(1)));

        assertEquals("""
                └── VerticalLayout[#transactionLayout, @style='width:100%;max-width:500px', @theme='padding spacing']
                    ├── Card[@style='width:100%']
                    │   ├── Span[text='29/01/2026', @slot='subtitle']
                    │   ├── Avatar[@slot='header-prefix']
                    │   └── Span[text='26,65 €', @slot='header-suffix', @theme='badge success']
                    ├── Card[@style='width:100%']
                    │   ├── Span[text='12/12/2025', @slot='subtitle']
                    │   ├── Avatar[@slot='header-prefix']
                    │   └── Span[text='20,54 €', @slot='header-suffix', @theme='badge success']
                    ├── Card[@style='width:100%']
                    │   ├── Span[text='18/12/2025', @slot='subtitle']
                    │   ├── Avatar[@slot='header-prefix']
                    │   └── Span[text='4,53 €', @slot='header-suffix', @theme='badge success']
                    ├── Card[@style='width:100%']
                    │   ├── Span[text='15/12/2025', @slot='subtitle']
                    │   ├── Avatar[@slot='header-prefix']
                    │   └── Span[text='54,70 €', @slot='header-suffix', @theme='badge success']
                    └── Card[@style='width:100%']
                        ├── Span[text='15/12/2025', @slot='subtitle']
                        ├── Avatar[@slot='header-prefix']
                        └── Span[text='-50,00 €', @slot='header-suffix', @theme='badge error']
                """, PrettyPrintTreeKt.toPrettyTree(verticalLayouts.get(2)));
    }

    private record TestStateConfig(String jdbcUrl) implements StateConfig {
            private TestStateConfig(String jdbcUrl) {
                this.jdbcUrl = "jdbc:h2:mem:" + jdbcUrl + ";MODE=Oracle;DEFAULT_NULL_ORDERING=HIGH";
            }

            @Override
            public String userName() {
                return null;
            }

            @Override
            public String password() {
                return null;
            }
        }
}
