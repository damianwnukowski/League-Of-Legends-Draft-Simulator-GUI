package main.draft.simulator;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.*;

public class DraftScreenController {

    @FXML
    Label champion0,champion1,champion2,champion3,champion4,champion5,champion6,champion7,champion8,champion9,
            champion10,champion11,champion12,champion13,champion14,champion15,champion16,champion17,champion18,
            champion19, countdownClock, team1, team2, matchNameDraftController;
    @FXML
    Button readyButton, chooseButton;


    @FXML
    FlowPane championFlowPane;

    ArrayList<Label> champions;
    ArrayList<String> pickedChampions;
    Label activeChampion;

    private Timer timer;
    int turn;

    private Team team;
    private String url;

    public void handleReadyOnClick(){
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(Main.SERVER_URL + url + "/ready");
            ArrayList<NameValuePair> params = new ArrayList<>();
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

        } catch (Exception ex) {
            Alert a = new Alert(Alert.AlertType.ERROR, ex.toString());
        }

    }

    public void setDraft(Team team, String url){
        this.url = url;
        this.team = team;
        pickedChampions = new ArrayList<>();

        new Thread(() -> {
            HttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(Main.SERVER_URL+url);
            while(true)
            try {
                if(turn==21) //do not refresh after having retrieving last turn
                    break;
                Thread.sleep(500);
                HttpResponse response = httpclient.execute(httpGet);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    StringBuilder result = new StringBuilder();
                    try (Scanner scanner = new Scanner(entity.getContent())) {
                        while (scanner.hasNext()) {
                            result.append(scanner.next());
                        }
                    }

                    JSONObject jsonObject = new JSONObject(result.toString());

                    boolean team1Ready = jsonObject.getBoolean("team1Ready");
                    boolean team2Ready = jsonObject.getBoolean("team2Ready");

                    turn = jsonObject.getInt("turn");

                    JSONArray arr = jsonObject.getJSONArray("choices");
                    for(int i =0; i<turn-1; i++){
                        if(arr.getString(i)!=null){
                            pickedChampions.add(arr.getString(i));
                        }
                    }

                    //Because all UI changes should be made in UI thread
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                double timeRemaining = jsonObject.getDouble("timeRemaining");
                                if (timeRemaining > 0 & timeRemaining <= 27)
                                    countdownClock.setText(Integer.toString((int) timeRemaining));

                                matchNameDraftController.setText(jsonObject.getString("matchName"));

                                //green when our team is ready and enemies' isn't
                                //gray when our team is not ready
                                //do not show if both ready
                                if ((team1Ready && team2Ready) || team == Team.AUDIENCE) {
                                    readyButton.setVisible(false);
                                } else if (team == Team.TEAM1 && team1Ready) {
                                    readyButton.setStyle("-fx-background-color: #98FB98");
                                    readyButton.setVisible(true);
                                } else if (team == Team.TEAM1) {
                                    readyButton.setStyle("-fx-background-color: #515151");
                                    readyButton.setVisible(true);
                                } else if (team == Team.TEAM2 && team2Ready) {
                                    readyButton.setStyle("-fx-background-color: #98FB98");
                                    readyButton.setVisible(true);
                                } else if (team == Team.TEAM2) {
                                    readyButton.setStyle("-fx-background-color: #515151");
                                    readyButton.setVisible(true);
                                }

                                EventHandler<MouseEvent> emptyEvent = new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent event) {

                                    }
                                };

                                for (Label champion: champions){
                                    if(pickedChampions.contains(champion.getText())){
                                        champion.setOnMouseEntered(emptyEvent);
                                        champion.setOnMouseExited(emptyEvent);
                                        champion.setOnMouseClicked(emptyEvent);
                                        champion.setStyle("-fx-background-color: gray; -fx-text-fill: black");
                                    }
                                }

                                int turnMapping=-1;

                                if(turn>0 && turn<=20)
                                    turnMapping = jsonObject.getJSONArray("TURN_MAPPING").getInt(turn-1);


                                if(turnMapping!=-1){
                                    chooseButton.setVisible(true);
                                    if(team==Team.TEAM1 && turnMapping == 1){
                                        chooseButton.setStyle("-fx-background-color: #ffd700");
                                    }else if(team==Team.TEAM2 && turnMapping ==2){
                                        chooseButton.setStyle("-fx-background-color: #ffd700");
                                    }else{
                                        chooseButton.setStyle("-fx-background-color: #634c00");
                                    }
                                }

                                team1.setText(jsonObject.getString("team1"));
                                team2.setText(jsonObject.getString("team2"));


                                champion0.setText(jsonObject.getJSONArray("choices").getString(0));
                                champion1.setText(jsonObject.getJSONArray("choices").getString(1));
                                champion2.setText(jsonObject.getJSONArray("choices").getString(2));
                                champion3.setText(jsonObject.getJSONArray("choices").getString(3));
                                champion4.setText(jsonObject.getJSONArray("choices").getString(4));
                                champion5.setText(jsonObject.getJSONArray("choices").getString(5));
                                champion6.setText(jsonObject.getJSONArray("choices").getString(6));
                                champion7.setText(jsonObject.getJSONArray("choices").getString(7));
                                champion8.setText(jsonObject.getJSONArray("choices").getString(8));
                                champion9.setText(jsonObject.getJSONArray("choices").getString(9));
                                champion10.setText(jsonObject.getJSONArray("choices").getString(10));
                                champion11.setText(jsonObject.getJSONArray("choices").getString(11));
                                champion12.setText(jsonObject.getJSONArray("choices").getString(12));
                                champion13.setText(jsonObject.getJSONArray("choices").getString(13));
                                champion14.setText(jsonObject.getJSONArray("choices").getString(14));
                                champion15.setText(jsonObject.getJSONArray("choices").getString(15));
                                champion16.setText(jsonObject.getJSONArray("choices").getString(16));
                                champion17.setText(jsonObject.getJSONArray("choices").getString(17));
                                champion18.setText(jsonObject.getJSONArray("choices").getString(18));
                                champion19.setText(jsonObject.getJSONArray("choices").getString(19));
                            }catch (JSONException ex){
                                //in case something is in other format (like not a string)
                                //because empty draft has nulls instead of empty strings
                            }
                        }
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();

        initChampionFlowPane();
    }

    private void initChampionFlowPane(){
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(Main.SERVER_URL + "/api/champions");

            HttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                StringBuilder result = new StringBuilder();
                try (Scanner scanner = new Scanner(entity.getContent())) {
                    while (scanner.hasNext()) {
                        result.append(scanner.nextLine());
                    }
                }
                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("champions");

                champions = new ArrayList<>(jsonArray.length());
                Label champion;

                String cssstyle;
                if(team==Team.AUDIENCE) {
                    cssstyle = "-fx-background-color: gray; -fx-text-fill: black";
                    for (int i = 0; i < jsonArray.length(); i++) {
                        champion = new Label(jsonArray.getString(i));
                        champion.setStyle(cssstyle);
                        champions.add(champion);
                        championFlowPane.getChildren().add(champion);
                    }
                } else{
                    cssstyle = "-fx-background-color: white; -fx-text-fill: black";
                    for(int i=0; i<jsonArray.length(); i++){
                        champion = new Label(jsonArray.getString(i));
                        champion.setOnMouseEntered(new championOnMouseEntered(champion));
                        champion.setOnMouseExited(new championOnMouseExited(champion));
                        champion.setOnMouseClicked(new championOnMouseClicked(champion));
                        champion.setStyle(cssstyle);
                        champions.add(champion);
                        championFlowPane.getChildren().add(champion);
                    }
                }
            }
        }catch (Exception ex){
            Alert a = new Alert(Alert.AlertType.ERROR, ex.toString());
        }

    }

    private class championOnMouseEntered implements EventHandler<MouseEvent> {
        Label champion;
        public championOnMouseEntered(Label champion){
            this.champion = champion;
        }
        @Override
        public void handle(MouseEvent event) {
            if(champion!=activeChampion)
                champion.setStyle("-fx-background-color: #8ac3ff; -fx-text-fill: black");
        }
    }

    private class championOnMouseExited implements EventHandler<MouseEvent> {
        Label champion;
        public championOnMouseExited(Label champion){
            this.champion = champion;
        }
        @Override
        public void handle(MouseEvent event) {
            if(champion!=activeChampion)
                champion.setStyle("-fx-background-color: white; -fx-text-fill: black");
        }
    }

    private class championOnMouseClicked implements EventHandler<MouseEvent> {
        Label champion;
        public championOnMouseClicked(Label champion){
            this.champion = champion;
        }
        @Override
        public void handle(MouseEvent event) {
            if(champion!=activeChampion) {
                if(activeChampion!=null){
                    activeChampion.setStyle("-fx-background-color: white; -fx-text-fill: black");
                }
                champion.setStyle("-fx-background-color: #007BFF");
                activeChampion = champion;
            }else {
                activeChampion = null;
                champion.setStyle("-fx-background-color: white; -fx-text-fill: black");
            }
        }
    }

    public void handleChooseOnClick() {
        if(activeChampion==null)
            return;
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(Main.SERVER_URL + url + "/choose");
        ArrayList<NameValuePair> input = new ArrayList<>(2);
        input.add(new BasicNameValuePair("champion", activeChampion.getText()));
        input.add(new BasicNameValuePair("turn", Integer.toString(turn)));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(input, "UTF-8"));
            httpclient.execute(httpPost);
        }catch (Exception ex){
            Alert a = new Alert(Alert.AlertType.ERROR, ex.toString());
        }
    }

    public enum Team{
        TEAM1, TEAM2, AUDIENCE
    }
}
