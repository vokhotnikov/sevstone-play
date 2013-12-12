package models

trait ModelServices extends TestimonialsComponent with SpecimensComponent with ArticlesComponent
  with ImagesComponent with CategoriesComponent with DepositsPlacesComponent with ExpositionsComponent

trait ModelServicesComponent {
  val Services = new ModelServices {
    val TestimonialService = new TestimonialService()
    val SpecimenService = new SpecimenService()
    val ArticleService = new ArticleService()
    val ImageService = new ImageService()
    val CategoryService = new CategoryService()
    val DepositsPlaceService = new DepositsPlaceService()
    val ExpositionService = new ExpositionService()
  }
}