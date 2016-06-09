package net.demilich.metastone.gui.bestofdecks;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Map;

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

    public static final String NAME = "SynergiesMediator";

    private static Logger logger = LoggerFactory.getLogger(BestOfDecksMediator.class);

    private final BestOfDecksView view;

    private final BestOfResultsView resultsView;

    private final BestOfResultsWait  waitView;

    private  Map calcResults;

    public BestOfDecksMediator() {
        super(NAME);
        view = new BestOfDecksView();
        resultsView = new BestOfResultsView();
        waitView = new BestOfResultsWait();

    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleNotification(final INotification<GameNotification> notification) {

        switch (notification.getId()) {
            case REPLY_DECK_FORMATS:
                List<DeckFormat> deckFormats = (List<DeckFormat>) notification.getBody();
                view.injectDeckFormats(deckFormats);
                break;
            case BEST_OF_GET_RESULTS:
                calcResults = (Map) notification.getBody();
                break;
            case BEST_OF_RESULTS:
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        resultsView.FillTable(calcResults);
                        getFacade().sendNotification(GameNotification.SHOW_VIEW, resultsView);
                        waitView.getScene().getWindow().hide();
                    }
                });
                break;
            case BEST_OF_MAIN_VIEW:
                getFacade().sendNotification(GameNotification.SHOW_VIEW, view);
                break;
            case BEST_OF_WAIT_SHOW:
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        getFacade().sendNotification(GameNotification.SHOW_MODAL_DIALOG,waitView);
                    }
                });
                break;
            case BEST_OF_WAIT_UPDATE:
                String progress = (String) notification.getBody();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        waitView.setCenterLabel(progress);
                    }
                });
                break;
            case BEST_OF_WAIT_UPDATE_TOP:
                String topText = (String) notification.getBody();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        waitView.setTopLabel(topText);
                    }
                });
                break;
        }
    }

    @Override
    public List<GameNotification> listNotificationInterests() {
        List<GameNotification> notificationInterests = new ArrayList<GameNotification>();
        notificationInterests.add(GameNotification.REPLY_DECK_FORMATS);
        notificationInterests.add(GameNotification.BEST_OF_RESULTS);
        notificationInterests.add(GameNotification.BEST_OF_MAIN_VIEW);
        notificationInterests.add(GameNotification.BEST_OF_GET_RESULTS);
        notificationInterests.add(GameNotification.BEST_OF_WAIT_SHOW);
        notificationInterests.add(GameNotification.BEST_OF_WAIT_UPDATE);
        notificationInterests.add(GameNotification.BEST_OF_WAIT_UPDATE_TOP);
        return notificationInterests;
    }

        @Override
        public void onRegister () {
            getFacade().sendNotification(GameNotification.SHOW_VIEW, view);
            getFacade().sendNotification(GameNotification.REQUEST_DECK_FORMATS);
        }

}
