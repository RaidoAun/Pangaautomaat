package gui.pangaautomaat;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import gui.pangaautomaat.screens.MainMenu;

import java.util.ArrayList;

public class MainClass extends Game {
	public MainMenu MAINMENU;
	public Viewport viewport;
	public SpriteBatch batch;
	public int ScreenWidth = 800;
	public int ScreenHeight = 600;
	public Skin skin;
	public AssetsLoader assetsLoader;
	public InputMultiplexer inputMultiplexer;
	public int[] väärtused;
	public int[] dollarkogused;
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
		//nt 1000-dollarilisi on praegu 10 tükki, 500 dollarilisi 3 jne
		this.väärtused = new int[]{1000,500,200,100,50};
		this.dollarkogused = new int[]{10,3,5,3,6};

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
