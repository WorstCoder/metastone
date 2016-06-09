package net.demilich.metastone.gui.synergies;

import javafx.application.Platform;
import net.demilich.metastone.GameNotification;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.gui.bestofdecks.BestOfDecksView;
import net.demilich.metastone.gui.bestofdecks.BestOfResultsView;
import net.demilich.metastone.gui.bestofdecks.BestOfResultsWait;
import net.demilich.nittygrittymvc.Mediator;
import net.demilich.nittygrittymvc.interfaces.INotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SynergiesMediator extends Mediator<GameNotification> {

    public static final String NAME = "SynergiesMediator";

    private static Logger logger = LoggerFactory.getLogger(SynergiesMediator.class);

    private final SynergiesView view;

    public SynergiesMediator() {
        super(NAME);
        view = new SynergiesView();

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
