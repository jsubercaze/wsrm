package fr.tse.lt2c.satin.matrix.movie;

import static com.xuggle.xuggler.Global.DEFAULT_TIME_UNIT;

import java.awt.image.BufferedImage;
import java.util.List;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

/**
 * Generate movie for later visualization
 * 
 * @author Julien Subercaze
 * 
 *         22/02/2012
 */
public class CreateMovieFromFiles {

	public static void createMovieFromImages(List<BufferedImage> pictures) {
		int imgWidth, imgHeight;
		BufferedImage first = pictures.get(0);
		imgWidth = first.getWidth();
		imgHeight = first.getHeight();
		final IMediaWriter writer = ToolFactory.makeWriter("movie/rectangleExtraction.mov");
		final int videoStreamIndex = 0;
		final int videoStreamId = 0;
		final int frameRate = 700000;
		long nextFrameTime = 0;
		writer.addVideoStream(videoStreamIndex, videoStreamId, imgWidth, imgHeight);
		for (int i = 0; i < pictures.size(); i++) {
			BufferedImage frame = pictures.get(i);
			writer.encodeVideo(videoStreamIndex, frame, nextFrameTime, DEFAULT_TIME_UNIT);
			nextFrameTime += frameRate;
		}
		pictures.clear();
		writer.close();
	}
}
