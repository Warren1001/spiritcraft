package com.kabryxis.spiritcraft;

import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GridRenderer extends MapRenderer {
	
	private final Image[][] gridImages = new BufferedImage[3][3];
	
	private final Image background;
	
	private boolean hasRendered = false;
	
	public GridRenderer(Plugin plugin) {
		try {
			background = ImageIO.read(new File(plugin.getDataFolder(), "images" + File.separator + "grid.jpg"));
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		if(hasRendered) return;
		canvas.drawImage(0, 0, background);
		for(int x = 0; x < gridImages.length; x++) {
			Image[] gridImagesY = gridImages[x];
			for(int y = 0; y < gridImagesY.length; y++) {
				Image gridImage = gridImagesY[y];
				if(gridImage != null) {
					int width = gridImage.getWidth(null);
					int height = gridImage.getHeight(null);
					byte[] bytes = MapPalette.imageToBytes(gridImage);
					for(int px = 0; px < width; px++) {
						for(int py = 0; py < height; py++) {
							byte b = bytes[py * width + px];
							if(b != 0) canvas.setPixel(x * 44 + px, y * 44 + py, b);
						}
					}
				}
			}
		}
		hasRendered = true;
	}
	
	public void setGridImage(int x, int y, Image image) {
		Validate.isTrue(x >= 0 && x <= 2, "x must be between 0 and 2 inclusively");
		Validate.isTrue(y >= 0 && y <= 2, "y must be between 0 and 2 inclusively");
		Validate.isTrue(image.getHeight(null) == 40, "image height must be 40 pixels");
		Validate.isTrue(image.getWidth(null) == 40, "image width must be 40 pixels");
		gridImages[x][y] = image;
	}
	
}
