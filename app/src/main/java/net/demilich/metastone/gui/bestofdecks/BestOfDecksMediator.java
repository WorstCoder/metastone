package net.demilich.metastone.gui.bestofdecks;

        import java.util.ArrayList;
        import java.util.List;

        import jdk.nashorn.internal.runtime.Debug;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;

        import net.demilich.nittygrittymvc.Mediator;
        import net.demilich.nittygrittymvc.interfaces.INotification;
        import javafx.application.Platform;
        import net.demilich.metastone.GameNotification;
        import net.demilich.metastone.game.decks.Deck;
        import net.demilich.metastone.game.decks.DeckFormat;
        import net.demilich.metastone.utils.Tuple;

public class BestOfDecksMediator extends Mediator<GameNotification> {

    public static final String NAME = "BestOfDecksMediator";

    private static Logger logger = LoggerFactory.getLogger(BestOfDecksMediator.class);

    private final BestOfDecksView view;

    public BestOfDecksMediator() {
        super(NAME);
        view = new BestOfDecksView();
        System.out.println(":)");

    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleNotification(final INotification<GameNotification> notification) {
        switch (notification.getId()) {
            case REPLY_DECK_FORMATS:
                List<DeckFormat> deckFormats = (List<DeckFormat>) notification.getBody();
                view.injectDeckFormats(deckFormats);
                break;

        }
    }

    @Override
    public List<GameNotification> listNotificationInterests() {
        List<GameNotification> notificationInterests = new ArrayList<GameNotification>();
        notificationInterests.add(GameNotification.REPLY_DECK_FORMATS);
        return notificationInterests;
    }

        @Override
        public void onRegister () {
            getFacade().sendNotification(GameNotification.SHOW_VIEW, view);
            getFacade().sendNotification(GameNotification.REQUEST_DECK_FORMATS);
        }

}
