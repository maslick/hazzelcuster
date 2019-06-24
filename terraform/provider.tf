provider "google" {
  credentials = "${file("./creds/key.json")}"
  project     = "hazelcuster"
  region      = "europe-west3"
}
