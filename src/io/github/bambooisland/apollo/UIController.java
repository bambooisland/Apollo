package io.github.bambooisland.apollo;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.controlsfx.control.RangeSlider;

import io.github.bambooisland.apollo.PlayerManager.RepeatStatus;
import io.github.bambooisland.trainmerody.base.Element;
import io.github.bambooisland.trainmerody.base.Table;
import io.github.bambooisland.trainmerody.base.VoidOperationException;
import io.github.bambooisland.trainmerody.poi.PoiTable;
import io.github.bambooisland.trainmerody.search.Extractor;
import io.github.bambooisland.trainmerody.search.Operators;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.util.Callback;
import javafx.util.Duration;

public class UIController implements Initializable {
	private Table table;
	private ApolloSong[] songs;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			table = PoiTable.getTable(new File("data/list.xlsx"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		songs = new ApolloSong[table.getNumberOfElements()];
		for (int i = 0; i < table.getNumberOfElements(); i++) {
			Element ele = table.getElement(i);
			songs[i] = new ApolloSong(
					ele.getField(0).getValue(), ele.getField(1).getValue(), ele.getField(2).getValue(),
					ele.getField(3).getValue(), ele.getField(4).getValue()
			);
		}
		
		Extractor ex = Extractor.getExtractor(table);
		
		ObservableList<String> list_tab1 = FXCollections.observableArrayList();
		for (String s : ex.getAllKindsOfValues(0)) {
			list_tab1.add(s);
		}
		Collections.sort(list_tab1, (str1, str2) -> {
			String cache1 = str1;
			String cache2 = str2;
			if (str1.toLowerCase().startsWith("the ")) {
				cache1 = str1.substring(4);
			}
			if (str2.toLowerCase().startsWith("the ")) {
				cache2 = str2.substring(4);
			}
			return Collator.getInstance().compare(cache1, cache2);
		});
		this.artistList_tab1.setItems(list_tab1);
		this.artistList_tab3.setItems(list_tab1);
		
		ObservableList<String> list_tab2 = FXCollections.observableArrayList();
		for (String s : ex.getAllKindsOfValues(1)) {
			list_tab2.add(s);
		}
		Collections.sort(list_tab2);
		this.albumList_tab2.setItems(list_tab2);
		
		ObservableList<ApolloSong> list_tab4 = FXCollections.observableArrayList();
		for (ApolloSong song : songs) {
			list_tab4.add(song);
		}
		Collections.sort(list_tab4, (song1, song2) -> {
			return Collator.getInstance().compare(song1.getTitle(), song2.getTitle());
		});
		this.songList_tab4.setItems(list_tab4);
		
		titleLabel.textProperty().bind(PlayerManager.currentSongTitleProperty());
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(titleLabel.textProperty());
		titleLabel.setTooltip(tooltip);
		
		artistLabel.textProperty().bind(PlayerManager.currentSongArtistProperty());
		tooltip = new Tooltip();
		tooltip.textProperty().bind(artistLabel.textProperty());
		artistLabel.setTooltip(tooltip);
		
		albumLabel.textProperty().bind(PlayerManager.currentSongAlbumProperty());
		tooltip = new Tooltip();
		tooltip.textProperty().bind(albumLabel.textProperty());
		albumLabel.setTooltip(tooltip);
		
		rangeSlider.lowValueProperty().addListener( (value, oldValue, newValue) -> {
			PlayerManager.setStartTime(newValue.doubleValue());
		});
		rangeSlider.highValueProperty().addListener( (value, oldValue, newValue) -> {
			PlayerManager.setStopTime(newValue.doubleValue());
		});
		
		playListView.itemsProperty().bind(PlayerManager.playListProperty());
		
		numberOfMusicLabel.setText(
			ex.getAllKindsOfValues(0).length + "人のアーティスト "
					+ ex.getAllKindsOfValues(1).length + "枚のアルバム 全"
					+ table.getNumberOfElements() + "曲"
		);
		
		Callback<ListView<ApolloSong>, ListCell<ApolloSong>> callback = list -> {
			return new ListCell<>() {
				@Override
				protected void updateItem(ApolloSong item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setGraphic(null);
					} else {
						setText(item.toString());
						setTooltip(new Tooltip(item.getArtist()
								+ "\n\t- " + item.getAlbum()
								+ " ( track" + item.getTrack() + " )"
						));
					}
				}
			};
		};
		songList_tab1.setCellFactory(callback);
		songList_tab2.setCellFactory(callback);
		songList_tab3.setCellFactory(callback);
		songList_tab4.setCellFactory(callback);
		playListView.setCellFactory(callback);
	}
	
	@FXML
	private Tab tab1;
	
	@FXML
	private Tab tab2;
	
	@FXML
	private Tab tab3;
	
	@FXML
	private Tab tab4;
	
	@FXML
	private ListView<String> artistList_tab1;
	
	@FXML
	private ListView<String> albumList_tab1;
	
	@FXML
	private ListView<ApolloSong> songList_tab1;
	
	@FXML
	private ListView<String> albumList_tab2;
	
	@FXML
	private ListView<ApolloSong> songList_tab2;
	
	@FXML
	private ListView<String> artistList_tab3;

	@FXML
	private ListView<ApolloSong> songList_tab3;
	
	@FXML
	private ListView<ApolloSong> songList_tab4;
	
	@FXML
	private ListView<ApolloSong> playListView;
	
	@FXML
	private Slider seekSlider;
	
	@FXML
	private Label titleLabel;
	
	@FXML
	private Label artistLabel;
	
	@FXML
	private Label albumLabel;
	
	@FXML
	private Label nowTimeLabel;
	
	@FXML
	private Label totalTimeLabel;
	
	@FXML
	private RangeSlider rangeSlider;
	
	@FXML
	private Label startTimeLabel;
	
	@FXML
	private Label stopTimeLabel;
	
	@FXML
	private Label numberOfMusicLabel;
	
	@FXML
	private Button backSongButton;
	
	@FXML
	private Button repeatButton;
	
	public static ListView<ApolloSong> getPlayListView() {
		return Main.con.playListView;
	}
	public static boolean seekSliderIsChanging() {
		return Main.con.seekSlider.isValueChanging() || Main.con.seekSlider.isPressed();
	}
	public static void setSeekSliderValue(double value) {
		Main.con.seekSlider.setValue(value);
	}
	public static DoubleProperty seekSliderValueProperty() {
		return Main.con.seekSlider.valueProperty();
	}
	public static void setNowTimeLabelText(String text) {
		Main.con.nowTimeLabel.setText(text);
	}
	public static void setTotalTimeLabelText(String text) {
		Main.con.totalTimeLabel.setText(text);
	}
	public static void setStartTimeLabelText(String text) {
		Main.con.startTimeLabel.setText(text);
	}
	public static void setStopTimeLabelText(String text) {
		Main.con.stopTimeLabel.setText(text);
	}
	public static double getRangeSliderLowValue() {
		return Main.con.rangeSlider.lowValueProperty().get();
	}
	public static double getRangeSliderHighValue() {
		return Main.con.rangeSlider.highValueProperty().get();
	}
	
	public static void scrollPlayListView(ApolloSong song) {
		ListView<ApolloSong> view = Main.con.playListView;
		view.getSelectionModel().select(song);
		List<ApolloSong> list = view.getItems();
		for (ScrollBar bar : getScrollBars(view)) {
			if (bar.getOrientation().equals(Orientation.VERTICAL)) {
				new Thread( () -> {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Platform.runLater( () -> {
						double d = list.size() > 1 ? (double)list.indexOf(song) / (list.size() - 1) : 0;
						bar.setValue(d);
					});
				}).start();
			}
		}
	}
	
	private static List<ScrollBar> getScrollBars(ListView<?> view) {
		List<ScrollBar> list = new ArrayList<>();
		for (Node node : view.lookupAll(".scroll-bar")) {
			try {
				ScrollBar bar = (ScrollBar)node;
				list.add(bar);
			} catch (ClassCastException e) {
				continue;
			}
		}
		return list;
	}
	
	private static void resetScrollBars(ListView<?> view) {
		for (ScrollBar bar : getScrollBars(view)) {
			bar.setValue(0);
		}
	}
	
	@FXML
	private void artistClicked(MouseEvent event) {
		try {
			if (tab1.isSelected()) {
				String artist = artistList_tab1.getSelectionModel().getSelectedItem();
				if (artist == null) {
					return;
				}
				ObservableList<String> list = FXCollections.observableArrayList();
				for (String album : Extractor.getExtractor(table).where(0, Operators.EQUAL, artist).getAllKindsOfValues(1)) {
					list.add(album);
				}
				albumList_tab1.setItems(list);
				songList_tab1.setItems(FXCollections.emptyObservableList());
				resetScrollBars(albumList_tab1);
			} else if (tab3.isSelected()) {
				String artist = artistList_tab3.getSelectionModel().getSelectedItem();
				if (artist == null) {
					return;
				}
				ObservableList<ApolloSong> list = FXCollections.observableArrayList();
				for (ApolloSong song : songs) {
					if (song.getArtist().equals(artist)) {
						list.add(song);
					}
				}
				songList_tab3.setItems(list);
				resetScrollBars(songList_tab3);
			}
		} catch (VoidOperationException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void albumClicked(MouseEvent event) {
		ListView<String> view = null;
		ListView<ApolloSong> target = null;
		if (tab1.isSelected()) {
			view = albumList_tab1;
			target = songList_tab1;
		} else if (tab2.isSelected()) {
			view = albumList_tab2;
			target = songList_tab2;
		}
		String album = view.getSelectionModel().getSelectedItem();
		if (album == null) {
			return;
		}
		ObservableList<ApolloSong> list = FXCollections.observableArrayList();
		for (ApolloSong song : songs) {
			if (tab1.isSelected()) {
				if (!artistList_tab1.getSelectionModel().getSelectedItem().equals(song.getArtist())) {
					continue;
				}
			}
			
			if (song.getAlbum().equals(album)) {
				list.add(song);
			}
		}
		Collections.sort(list);
		target.setItems(list);
		resetScrollBars(target);
		return;
	}
	
	@FXML
	private void songClicked(MouseEvent event) throws UnsupportedEncodingException {
		if (event.getClickCount() < 2) {
			return;
		}
		ListView<ApolloSong> view = null;
		if (tab1.isSelected()) {
			view = songList_tab1;
		} else if (tab2.isSelected()) {
			view = songList_tab2;
		} else if (tab3.isSelected()) {
			view = songList_tab3;
		} else if (tab4.isSelected()) {
			view = songList_tab4;
		}
		PlayerManager.setPlaylist(view);
	}
	
	@FXML
	private void playListClicked(MouseEvent event) throws UnsupportedEncodingException {
		if (event.getClickCount() < 2) {
			return;
		}
		PlayerManager.setPlaylist(playListView);
	}
	
	@FXML
	private void pauseAction(MouseEvent event) {
		MediaPlayer player;
		if ((player = PlayerManager.getCurrentPlayer()) == null) {
			return;
		}
		if (player.getStatus() == MediaPlayer.Status.PLAYING) {
			player.pause();
		} else {
			player.play();
		}
	}
	
	private static boolean cache;
	
	@FXML
	private void nextSongAction(MouseEvent event) {
		MediaPlayer player;
		if ((player = PlayerManager.getCurrentPlayer()) == null) {
			return;
		}
		if (event.getClickCount() == 1) {
			if (player.getStatus() == MediaPlayer.Status.PLAYING) {
				cache = true;
			} else {
				cache = false;
			}
		}
		PlayerManager.nextSong(cache);
	}
	
	@FXML
	private void backSongAction(MouseEvent event) {
		MediaPlayer player;
		if ((player = PlayerManager.getCurrentPlayer()) == null) {
			return;
		}
		if (event.getClickCount() >= 2) {
			if (event.getClickCount() == 2) {
				if (player.getStatus() == MediaPlayer.Status.PLAYING) {
					cache = true;
				} else {
					cache = false;
				}
			}
			PlayerManager.backSong(cache);
		} else {
			PlayerManager.getCurrentPlayer().seek(new Duration(0));
		}
	}
	
	@FXML
	private void repeatChanged(MouseEvent event) {
		switch (PlayerManager.getRepeatStatus()) {
		case LIST:
			PlayerManager.setRepeatStatus(RepeatStatus.SHUFFLE);
			repeatButton.setText("🔀");
			backSongButton.setVisible(false);
			break;
		case SHUFFLE:
			PlayerManager.setRepeatStatus(RepeatStatus.ONE);
			repeatButton.setText("🔂");
			break;
		case ONE:
			PlayerManager.setRepeatStatus(RepeatStatus.LIST);
			repeatButton.setText("🔁");
			backSongButton.setVisible(true);
			break;
		}
	}
}
