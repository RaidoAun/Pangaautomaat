package gui.pangaautomaat;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import gui.pangaautomaat.screens.MainMenu;

public class MainClass extends Game {
	public MainMenu MAINMENU;
	public Viewport viewport;
	public SpriteBatch batch;
	public int ScreenWidth = 800;
	public int ScreenHeight = 600;
	public Skin skin;
	public AssetsLoader assetsLoader;
	public InputMultiplexer inputMultiplexer;
	public Prefs prefs;
	public DataDownloader dataDownloader;
	public static final String[] valuutad = new String[]{"USD","GBP","CHF","SEK","RUB"};
	@Override
	public void create () {
		this.viewport = new FitViewport(this.ScreenWidth,this.ScreenHeight, new OrthographicCamera(this.ScreenWidth,this.ScreenHeight));
		this.batch = new SpriteBatch();
		this.assetsLoader = new AssetsLoader();
		this.assetsLoader.init();
		this.assetsLoader.load();
		this.skin = assetsLoader.manager.get(assetsLoader.uiSkinJson,Skin.class);
		this.dataDownloader = new DataDownloader();
		this.prefs = new Prefs();
		this.prefs.init();
		updateDataToPrefs();
		System.out.println(dataDownloader.dataTime);

		this.inputMultiplexer = new InputMultiplexer();
		this.inputMultiplexer.addProcessor(new InputAdapter(){
			@Override
			public boolean keyDown (int keycode) {
				if (keycode == Input.Keys.ESCAPE){
					return true;
				}
				return false;
			}

		});
		Gdx.input.setInputProcessor(this.inputMultiplexer);


        this.MAINMENU = new MainMenu(this);
		this.setScreen(this.MAINMENU);
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose () {
		batch.dispose();
		skin.dispose();
	}
	public void updateDataToPrefs(){
		String[] strings = this.dataDownloader.getData();
		for (int i = 0; i < strings.length; i++) {
			String[] valuutaandmed = strings[i].split(",");
			this.prefs.preferences.putFloat(this.prefs.EURPREFIX+ valuutaandmed[0], Float.parseFloat(valuutaandmed[1]));
		}
		this.prefs.save();
	}

}
