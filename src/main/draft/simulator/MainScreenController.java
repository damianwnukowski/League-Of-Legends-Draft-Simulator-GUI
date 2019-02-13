package main.draft.simulator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MainScreenController {

    @FXML
    private Button createDraftButton,joinDraftButton;
    @FXML
    private GridPane gridPane;

    @FXML
    private VBox createDraftVBox, joinDraftVBox, urlsVBox;

    @FXML
    private TextField firstTeam, secondTeam, matchName, draftUrl, team1Url, team2Url, audienceUrl;


    public void handleCreateDraftOnClick(){
        createDraftButton.setStyle("-fx-background-color: #CCCCCC");
        joinDraftButton.setStyle("-fx-background-color: #515151");

        createDraftVBox.setVisible(true);
        joinDraftVBox.setVisible(false);
        urlsVBox.setVisible(false);
    }

    public void handleJoinDraftOnClick(){
        joinDraftButton.setStyle("-fx-background-color: #CCCCCC");
        createDraftButton.setStyle("-fx-background-color: #515151");

        joinDraftVBox.setVisible(true);
        createDraftVBox.setVisible(false);
        urlsVBox.setVisible(false);
    }

    public void handleJoinTeam1OnClick(){
        joinTeam(team1Url.getText());
    }

    public void handleJoinTeam2OnClick(){
        joinTeam(team2Url.getText());
    }

    public void joinTeam(String url){
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(Main.SERVER_URL + url + "/whichTeam");

            HttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            DraftScreenController.Team team = DraftScreenController.Team.TEAM1; //Method will return if no team was verified

            if (entity != null) {
                StringBuilder result = new StringBuilder();
                try (Scanner scanner = new Scanner(entity.getContent())) {
                    while (scanner.hasNext()) {
                        result.append(scanner.next());
                    }
                }
                if(result.toString().equals("team1")){
                    team = DraftScreenController.Team.TEAM1;
                }else if(result.toString().equals("team2")){
                    team = DraftScreenController.Team.TEAM2;
                }else if(result.toString().equals("audience")){
                    team = DraftScreenController.Team.AUDIENCE;
                }else{
                    Alert a = new Alert(Alert.AlertType.ERROR, "Wrong url");
                    a.show();
                    return;
                }
            }


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/views/draftScreen.fxml"));
            Parent root = loader.load();
            DraftScreenController controller = loader.getController();
            controller.setDraft(team, url);


            gridPane.getScene().setRoot(root);
        }catch (Exception ex){
            Alert a = new Alert(Alert.AlertType.ERROR, ex.toString(), ButtonType.OK);
            a.show();
        }
    }

    public void handleCreateDraftOkOnClick(){
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(Main.SERVER_URL+"/api/draft/createDraft");

            List<NameValuePair> params = new ArrayList<>(3);
            params.add(new BasicNameValuePair("team1", firstTeam.getText()));
            params.add(new BasicNameValuePair("team2", secondTeam.getText()));
            params.add(new BasicNameValuePair("matchName", matchName.getText()));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                StringBuilder result = new StringBuilder();
                try (Scanner scanner = new Scanner(entity.getContent())) {
                    while(scanner.hasNext()){
                        result.append(scanner.next());
                    }
                }
                JSONObject obj = new JSONObject(result.toString());
                team1Url.setText(obj.getString("team1url"));
                team2Url.setText(obj.getString("team2url"));
                audienceUrl.setText(obj.getString("audienceUrl"));
                joinDraftVBox.setVisible(false);
                createDraftVBox.setVisible(false);
                createDraftButton.setStyle("-fx-background-color: #515151");
                urlsVBox.setVisible(true);
            }

        }catch (Exception ex){
            Alert a = new Alert(Alert.AlertType.ERROR, ex.toString(), ButtonType.OK);
            a.show();
        }
    }

    public void handleJoinDraftOkOnClick(){
        joinTeam(draftUrl.getText());
    }
}
