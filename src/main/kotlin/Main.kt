package ind.glowingstone

val picGenerate = PicGenerate("Glowingstone124")
fun main() {
	picGenerate.generatePerlinPNG(width = 256, height = 256, octaves = 8, filename = "perlin.jpg")
}