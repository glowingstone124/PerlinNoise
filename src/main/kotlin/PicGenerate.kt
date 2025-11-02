package ind.glowingstone

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class PicGenerate(val seed:String) {

	val perlinNoise = PerlinNoise(seed)
	fun noiseToColor(value: Double): Color {
		val r = ((value + 1) / 2 * 255).toInt().coerceIn(0, 255)
		val g = ((1 - value) / 2 * 255).toInt().coerceIn(0, 255)
		val b = (0.5 * 255).toInt()
		return Color(r, g, b)
	}

	fun generatePerlinPNG(width: Int, height: Int, octaves: Int, filename: String) {
		val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
		for (y in 0 until height) {
			for (x in 0 until width) {
				val nx = x.toDouble() / (width - 1)
				val ny = y.toDouble() / (height - 1)
				val value = perlinNoise.PSigma(Vec2D(nx, ny), octaves)
				val color = noiseToColor(value)
				image.setRGB(x, y, color.rgb)
			}
		}
		ImageIO.write(image, "jpg", File(filename))
	}
}