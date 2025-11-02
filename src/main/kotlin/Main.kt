package ind.glowingstone

val picGenerate = PicGenerate("Glowingstone124")
fun main() {
	picGenerate.generatePerlinPNG(width = 2048, height = 2048, octaves = 8, filename = "perlin.jpg")
}