package org.cyanotic.cx10.team2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.imageio.ImageIO;

public final class ImageUtil {
	
	private static final String PNG_IMAGE_FORMAT = "png";

	private ImageUtil() {
		
	}
	
	public static Optional<IOException> saveAsPng(BufferedImage image, String targetFile) {
		return saveAsPng(image, new File(targetFile));
	}
	
	public static Optional<IOException> saveAsPng(BufferedImage image, File target) {
		try {
			ImageIO.write(image, PNG_IMAGE_FORMAT, target);
			return Optional.empty();
		} catch (IOException exception) {
			return Optional.of(exception);
		}
	}
}
