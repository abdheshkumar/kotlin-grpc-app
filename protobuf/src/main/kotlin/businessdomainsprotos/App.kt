package businessdomainsprotos

import examplepb.Person
import examplepb.PersonKt.location
import examplepb.person
import io.envoyproxy.pgv.ReflectiveValidatorIndex
import io.envoyproxy.pgv.Validator
import io.envoyproxy.pgv.ValidatorIndex

object App {
    @JvmStatic
    fun main(args: Array<String>) {
        // Create a validator index that reflectively loads generated validators
        val index: ValidatorIndex = ReflectiveValidatorIndex()
        // Assert that a message is valid
        val personMessage = Person.newBuilder()
            .setId(22221)
            .setEmail("abc@example.com")
            .setName("Protocol Buffer")
            .setHome(Person.Location.newBuilder().setLat(67.0).setLng(78.0))
            .build()
        val result: Validator<Person> = index.validatorFor(personMessage)
        result.assertValid(personMessage)

        val p = person {
            name = "Protocol Buffer"
            email = "abc@example.com"
            id = 22221
            home = location {
                lat = 67.0
                lng = 78.0
            }
        }
        index.validatorFor<Person>(p).assertValid(p)
    }
}
