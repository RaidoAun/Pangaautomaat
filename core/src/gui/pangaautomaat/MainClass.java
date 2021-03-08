package gui.pangaautomaat;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import gui.pangaautomaat.screens.MainMenu;

import java.util.Arrays;

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

	@Override
	public void create () {
		this.viewport = new FitViewport(this.ScreenWidth,this.ScreenHeight, new OrthographicCamera(this.ScreenWidth,this.ScreenHeight));
		this.batch = new SpriteBatch();
		this.assetsLoader = new AssetsLoader();
		this.assetsLoader.init();
		this.assetsLoader.load();
		this.skin = assetsLoader.manager.get(assetsLoader.uiSkinJson,Skin.class);

		this.prefs = new Prefs();
		this.prefs.init();

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

		DataDownloader dataDownloader = new DataDownloader();
		dataDownloader.download2();
		System.out.println(Arrays.toString(dataDownloader.getData()));

        this.MAINMENU = new MainMenu(this);
		this.setScreen(this.MAINMENU);
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose () {
	}


}
