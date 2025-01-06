package com.friends.common.annotation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [NullOrNotBlankValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class NullOrNotBlank(
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class NullOrNotBlankValidator : ConstraintValidator<NullOrNotBlank, String?> {
    override fun initialize(contactNumber: NullOrNotBlank) {}

    override fun isValid(
        contactField: String?,
        cxt: ConstraintValidatorContext?,
    ): Boolean = contactField == null || contactField.isNotBlank()
}
