package ind.glowingstone

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.floor
import kotlin.math.pow
import kotlin.time.measureTime


fun lerp(a: Double, b: Double, t: Double): Double = a + (b - a) * t

fun S(t: Double) = 6 * t.pow(5) - 15 * t.pow(4) + 10 * t.pow(3)

data class Vec2D(val x: Double, val y: Double) {
	infix fun dot(v: Vec2D) = x * v.x + y * v.y
}

fun generateRandomGradient(input: Vec2D): Vec2D {
	var seed = input.x.toInt() * 1836311903L xor (input.y.toInt() * 2971215073L)
	seed = seed xor (seed shr 13)
	val angle = (seed % 360).toDouble()
	val rad = Math.toRadians(angle)
	return Vec2D(kotlin.math.cos(rad), kotlin.math.sin(rad))
}

val A = 0.5

fun getPointU(input: Vec2D): Double{
	return input.x - floor(input.x)
}

fun getPointV(input: Vec2D): Double{
	return input.y - floor(input.y)
}

fun calculateN(input: Vec2D): Double{
	val i = floor(input.x)
	val j = floor(input.y)
	val g00 = generateRandomGradient(Vec2D(i, j))
	val g01 = generateRandomGradient(Vec2D(i, j + 1))
	val g10 = generateRandomGradient(Vec2D(i + 1, j))
	val g11 = generateRandomGradient(Vec2D(i + 1, j + 1))
	val d00 = g00 dot Vec2D(getPointU(input), getPointV(input))
	val d01 = g01 dot Vec2D(getPointU(input), getPointV(input) - 1)
	val d10 = g10 dot Vec2D(getPointU(input) - 1, getPointV(input))
	val d11 = g11 dot Vec2D(getPointU(input) - 1, getPointV(input) -1)

	return lerp(lerp(d00, d10, S(getPointU(input))), lerp(d01,d11,S(getPointU(input))), S(getPointV(input)))
}

fun PSigma(input: Vec2D, octaves: Int): Double {
	var result = 0.0
	for (i: Int in 0..<octaves) {
		result += A.pow(i) * calculateN(Vec2D(2.0.pow(i.toDouble()) * input.x, 2.0.pow(i.toDouble()) * input.y))
	}
	return result
}
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
			val value = PSigma(Vec2D(nx, ny), octaves)
			val color = noiseToColor(value)
			image.setRGB(x, y, color.rgb)
		}
	}
	ImageIO.write(image, "jpg", File(filename))
}

fun main() {
	println(measureTime {
		generatePerlinPNG(width = 2048, height = 2048, octaves = 8, filename = "perlin.jpg")
	})
}
