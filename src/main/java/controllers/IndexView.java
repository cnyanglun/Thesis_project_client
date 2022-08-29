package controllers;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import model.ClientConServerThread;
import model.clientUser;
import org.tinylog.Logger;
import util.Message;
import util.User;
import util.tool.manageObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;


public class IndexView implements Initializable{

    @FXML
    private Label accountId;
    @FXML
    private Label nickName;
    @FXML
    private ImageView yourAvatar;
    @FXML
    private Label speakWith;
    @FXML
    private VBox friendList;
    @FXML
    private BorderPane chatInterface;
    @FXML
    private TextArea inputText;
    @FXML
    private TextArea displayText;
    @FXML
    private ScrollPane sp;
    @FXML
    private ImageView turnOff;
    @FXML
    private ImageView addFriend;
    @FXML
    private ImageView createGroup;
    @FXML
    private AnchorPane functionBar;
    @FXML
    private AnchorPane acp;
    @FXML
    private BorderPane win;
    @FXML
    private AnchorPane accountInfo;
//    private HashMap<String,Integer> friendUnreadMap = new HashMap<>();

    private Stage stage;

    boolean isOk = true;

    private ClientConServerThread ccst;

    String url;
    private User userInfo;
    private String friendId1;
//    HashMap<String,TextArea> hm = new HashMap<>();

    String[] imageUrl = {"/image/avatars/image1.jpg","/image/avatars/image2.jpg","/image/avatars/image3.jpg",
                        "/image/avatars/image4.jpg","/image/avatars/image5.jpg","/image/avatars/image6.jpg",
                        "/image/avatars/image7.jpg","/image/avatars/image8.jpg","/image/avatars/image9.jpg"
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //get the userInfo from Server

        init();
        initFriendList();
        addFriendFunction();
        createGroupFunction();
        turnOffFunction();
        userInfoModify();
    }
    private void init(){
        try {
            userInfo = ClientConServerThread.user;
            accountId.setText(userInfo.getAccount());
            nickName.setText(userInfo.getNickName());
            yourAvatar.setImage(new Image(userInfo.getImageUrl(),60,60,false,false));
        }catch (NullPointerException e){
            Logger.info("Your information is not perfect");
        }

        ClientConServerThread clientConServerThread = (ClientConServerThread) manageObject.getObject("clientConServerThread");
        ccst = clientConServerThread;

    }

    private void userInfoModify(){
        accountInfo.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                VBox accountInfoScene = new VBox();
                accountInfoScene.setAlignment(Pos.TOP_CENTER);
                accountInfoScene.setSpacing(20);
                accountInfoScene.setPadding(new Insets(50));

                Label changeName = new Label("CHANGE NAME");
                Label changeAvatar = new Label("CHANGE AVATAR");

                //Change name Function
                changeName.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        VBox changeNameScene = new VBox();
                        changeNameScene.setSpacing(20);
                        changeNameScene.setAlignment(Pos.CENTER);

                        Label changeTip = new Label("Please input a new Name");
                        Button change = new Button("CHANGE");
                        TextField newName = new TextField();
                        newName.setMaxWidth(200);
                        changeNameScene.getChildren().addAll(changeTip,newName,change);

                        win.setCenter(changeNameScene);

                        change.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                String newNameText = newName.getText();

                                try {
                                    Message changeNewName = Message.builder()
                                            .sender(userInfo.getAccount())
                                            .mesType("change_NewName")
                                            .con(newNameText).build();

                                    ccst.sendToServer(changeNewName);

                                    Thread.sleep(3000);

                                } catch (InterruptedException e) {
                                    if(isOk){
                                        Logger.info("Success to Change");
                                        nickName.setText(newNameText);
                                    }else {
                                        Logger.info("Failed to Change");
                                    }
                                }
                            }
                        });
                    }
                });

                //Change avatar function
                changeAvatar.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        VBox changeAvatar = new VBox();
                        changeAvatar.setSpacing(20);
                        changeAvatar.setAlignment(Pos.CENTER);
                        Button change = new Button("CHANGE");
                        Label changeResult = new Label();
                        FlowPane imageList = new FlowPane();
                        imageList.resize(300,450);
                        imageList.setMaxWidth(300);

                        imageList.setHgap(10);
                        imageList.setVgap(10);


                        for (int i = 0; i < Arrays.stream(imageUrl).count(); i++) {
                            Image image = new Image(imageUrl[i], 60, 60, false, false);
                            ImageView imageView = new ImageView(image);
                            int finalI = i;
                            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    yourAvatar.setImage(image);
                                    url = imageUrl[finalI];
                                }
                            });
                            imageList.getChildren().add(imageView);
                        }

                        change.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                Message change_avatars = Message.builder()
                                        .mesType("change_Avatar")
                                        .sender(userInfo.getAccount())
                                        .con(url).build();

                                ccst.sendToServer(change_avatars);

                                try {
                                    Thread.sleep(1000*60);
                                } catch (InterruptedException e) {

                                }

                                if(isOk){
                                    changeResult.setText("Success to Change");
                                }else
                                    changeResult.setText("Failed to Change");

                            }
                        });


                        changeAvatar.getChildren().addAll(change,imageList,changeResult);
                        win.setCenter(changeAvatar);
                    }
                });


                accountInfoScene.getChildren().addAll(changeName,changeAvatar);


                win.setCenter(accountInfoScene);
            }
        });
    }

    public void initFriendList(){
        chatInterface.setVisible(false);
        var list = userInfo.getFriendList();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
            User friendObject = (User)iterator.next();
            String friendId = friendObject.getAccount();

            createChat(friendId);
            HBox friend = createFriend(friendObject);

            friendList.getChildren().add(friend);
            friendList.setSpacing(5);
            friendList.setPadding(new Insets(5));
        }
    }

    public void createChat(String friendId){
        TextArea chat = new TextArea();
        chat.setEditable(false);
        chat.setPrefSize(537,288);

        //load chat Record
        for (User friend : userInfo.getFriendList()) {
            if(friend.getAccount().equals(friendId)){
                for (Message message : friend.getChatRecord()) {
                    chat.appendText(accountId.getText() + " say: \n" + message.getCon() + "\n" + "\n");

                }
            }
        }
//        System.out.println(userInfo.getUnreadCount());

        manageObject.addChat(friendId,chat);
    }

    private HBox createFriend(User friendObject){
        String friendId = friendObject.getAccount();

        Label friendAccountId = new Label("label");
        Label friendName = new Label("label");
        Label unreadCount = new Label();
        unreadCount.setText(" ");
        unreadCount.setTextFill(Color.RED);
        manageObject.addLabel(friendId,unreadCount);

        VBox friendInfo = new VBox();


        for (String element : userInfo.getUnreadCount().keySet()) {
            if(element.equals(friendId)){
                unreadCount.setText(String.valueOf(userInfo.getUnreadCount().get(element)));
                break;
            }
        }


        friendInfo.getChildren().addAll(friendAccountId,unreadCount,friendName);
        friendInfo.setPadding(new Insets(5));
        friendInfo.setSpacing(8);

        ImageView image;
        try {
            friendAccountId.setText(friendId);
            image = new ImageView(new Image(friendObject.getImageUrl(), 60, 60, false, false));
            friendName.setText(friendObject.getNickName());
        }catch (NullPointerException e){
            image = new ImageView(new Image("/image/image.jpg",60,60,false,false));
            friendName.setText("");
        }

        image.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                win.setCenter(chatInterface);
                friendId1 = friendId;
                chatInterface.setVisible(true);
                speakWith.setText(userInfo.getAccount() + " speak with " + friendId);
                displayText = manageObject.getChat(friendId);
                sp.setContent(displayText);

                unreadCount.setText(" ");
                Message clear_unread = Message.builder().mesType("clear_Unread").sender(userInfo.getAccount()).getter(friendId).build();
                ccst.sendToServer(clear_unread);
            }
        });


        HBox friend = new HBox();
        friend.resize(200,90);

        friend.getChildren().addAll(image,friendInfo);
        return friend;
    }

    private void addFriendFunction(){
        addFriend.setImage(new Image("/image/addfriend.png", 30, 30, false, false));
        addFriend.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //create a new Scene which use to search Friend
                VBox addFriendScene = new VBox();
                addFriendScene.setAlignment(Pos.TOP_CENTER);
                addFriendScene.setSpacing(20);
                addFriendScene.setPadding(new Insets(20));

                Label friendAccount = new Label("Please Input user's name");
                TextField InputFriendName = new TextField();
                InputFriendName.setMaxWidth(200);
                Button inquire = new Button("Inquire");

                VBox displayResult = new VBox();
                displayResult.setAlignment(Pos.CENTER);
                displayResult.setSpacing(40);

                inquire.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (!InputFriendName.getText().isEmpty()){
                            Message message = Message.builder()
                                    .sender(userInfo.getAccount())
                                    .getter(InputFriendName.getText())
                                    .mesType("search_Friend").build();
//                            Message message = new Message();
//                            message.setSender(userInfo.getAccount());
//                            message.setGetter(InputFriendName.getText());
//                            message.setMesType("search_Friend");



                            long count = displayResult.getChildren().stream().count();
                            int count1 = Integer.parseInt(String.valueOf(count));
                            if(count1 != 0){
                                displayResult.getChildren().remove(0,count1);
                            }

//                            System.out.println(displayResult.getChildren().stream().count());
                            AnchorPane friend = new AnchorPane();
//                                inquireResult = friend;
                            manageObject.addObject("inquireResult",friend);
                            friend.setMaxWidth(200);
                            friend.setPadding(new Insets(5));
                            ImageView pic = new ImageView(new Image("/image/image.jpg",80,80,false,false));
                            friend.resize(200,90);
                            Label friendId = new Label();
                            Label friendName = new Label();
                            AnchorPane.setRightAnchor(friendId,5.0);
                            AnchorPane.setTopAnchor(friendId,10.0);
                            AnchorPane.setRightAnchor(friendName,5.0);
                            AnchorPane.setBottomAnchor(friendName,15.0);
                            friend.getChildren().addAll(pic,friendId,friendName);
                            Button addbtn = new Button("add");

                            addbtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {

                                    String friendId = InputFriendName.getText();

                                    Message message1 = Message.builder()
                                            .sender(userInfo.getAccount())
                                            .mesType("add_Friend")
                                            .getter(friendId).build();

                                    ccst.sendToServer(message1);

                                    Label result = new Label();
                                    displayResult.getChildren().add(result);

                                    try {
                                        Thread.sleep(1000*60);
                                    } catch (InterruptedException e) {
//                                            throw new RuntimeException(e);
                                    }

                                    if(isOk){
                                        Logger.info("Success to Add");
                                        result.setText("Success to Add");

                                        //Create new friend and put it into friendList after adding friend
//                                        addFriendToFriendList(friendId);
                                    }else {
                                        Logger.info("Failed to add");
                                        result.setText("Failed to add");
                                    }

                                    refresh();
                                }
                                });

                                displayResult.getChildren().addAll(addbtn,friend);

                                ccst.sendToServer(message);
                                Logger.info("Send add friend request to Server:" + userInfo.getAccount() + " want to add " + InputFriendName.getText());

                        }
                        else
                            Logger.info("The text is null , please input the user you want to add!");
                    }
                });



                addFriendScene.getChildren().addAll(friendAccount,InputFriendName,inquire,displayResult);

                win.setCenter(addFriendScene);
            }
        });
    }

    private void turnOffFunction(){
        turnOff.setImage(new Image("/image/exit.png",30,30,false,false));
        turnOff.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Platform.exit();
                try {
                    Message exitMessage = Message.builder()
                                    .mesType("exit_Message").build();
                    ccst.sendToServer(exitMessage);

                    clientUser.socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void createGroupFunction(){
        createGroup.setImage(new Image("/image/img.png",30,30,false,false));
        createGroup.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

            }
        });
    }

    @FXML
    private void send(){
        if(!inputText.getText().isEmpty()){
            String content = inputText.getText();

            Message commonMessage = Message.builder()
                    .mesType("common_Message")
                    .sender(userInfo.getAccount())
                    .getter(friendId1)
                    .con(content).build();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
            String time = sdf.format(new Date());
            commonMessage.setSendTime(time);

            ccst.sendToServer(commonMessage);


            inputText.clear();

            displayText.appendText(accountId.getText() + " say: \n");
            displayText.appendText(content + "\n");
            displayText.appendText("\n");
        }
    }

    @SneakyThrows
    private void refresh(){
        FXMLLoader Loader = new FXMLLoader(getClass().getResource("/JavaFx/IndexView.fxml"));
        Parent root = Loader.load();
        IndexView IndexController = Loader.getController();
        manageObject.addObject("indexView",IndexController);
        stage = (Stage) ((Node)friendList).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void setIsOk(boolean a){
        this.isOk = a;
    }
    @FXML
    private void clearAll(){
        inputText.clear();
    }
}