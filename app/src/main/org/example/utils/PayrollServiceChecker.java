package org.example.utils;

import java.awt.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

public class PayrollServiceChecker {

  private static final DateTimeFormatter INPUT_DATE_FORMATTER =
      DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT);

  public static boolean isValidBirthDate(String value) {
    if (isBlankOrPlaceholder(value)) {
      Logger.success("Birthdate left blank or placeholder accepted.", false);
      return true;
    }

    try {
      LocalDate.parse(value.trim(), INPUT_DATE_FORMATTER);
      Logger.success("Birthdate format is valid.", false);
      return true;
    } catch (DateTimeParseException e) {
      Logger.error("Invalid birthday format.", true);
      return false;
    }
  }

  public static boolean isValidPhoneNumber(String value) {
    if (isBlankOrPlaceholder(value)) {
      Logger.success("Phone number left blank or placeholder accepted.", false);
      return true;
    }

    String trimmed = value.trim();

    // Format: XXX-XXX-XXX
    boolean isValid = trimmed.matches("\\d{3}-\\d{3}-\\d{3}");

    if (isValid) {
      Logger.success("Phone number format is valid.", false);
    } else {
      Logger.error("Phone number must follow the XXX-XXX-XXX format.", true);
    }

    return isValid;
  }

  public static boolean isValidSss(String value) {
    if (isBlankOrPlaceholder(value)) {
      Logger.success("SSS number left blank or placeholder accepted.", false);
      return true;
    }

    boolean isValid = value.trim().matches("\\d{2}-\\d{7}-\\d");
    if (isValid) {
      Logger.success("SSS number format is valid.", false);
    } else {
      Logger.error("Invalid SSS number.", true);
    }
    return isValid;
  }

  public static boolean isValidPhilHealth(String value) {
    if (isBlankOrPlaceholder(value)) {
      Logger.success("PhilHealth number left blank or placeholder accepted.", false);
      return true;
    }

    boolean isValid = value.trim().matches("\\d{12}");
    if (isValid) {
      Logger.success("PhilHealth number format is valid.", false);
    } else {
      Logger.error("Invalid PhilHealth number.", true);
    }
    return isValid;
  }

  public static boolean isValidTin(String value) {
    if (isBlankOrPlaceholder(value)) {
      Logger.success("TIN left blank or placeholder accepted.", false);
      return true;
    }

    boolean isValid = value.trim().matches("\\d{3}-\\d{3}-\\d{3}-\\d{3}");
    if (isValid) {
      Logger.success("TIN format is valid.", false);
    } else {
      Logger.error("Invalid TIN.", true);
    }
    return isValid;
  }

  public static boolean isValidPagIbig(String value) {
    if (isBlankOrPlaceholder(value)) {
      Logger.success("Pag-IBIG number left blank or placeholder accepted.", false);
      return true;
    }

    boolean isValid = value.trim().matches("\\d{12}");
    if (isValid) {
      Logger.success("Pag-IBIG number format is valid.", false);
    } else {
      Logger.error("Invalid Pag-IBIG number.", true);
    }
    return isValid;
  }

  public static boolean isValidNumericValue(String value) {
    if (isBlankOrPlaceholder(value)) {
      Logger.success("Numeric field left blank or placeholder accepted.", false);
      return true;
    }

    String trimmed = value.trim();
    boolean isValid = false;

    // Allows only digits, commas, and one decimal point
    if (trimmed.matches("\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?|\\d+(?:\\.\\d+)?")) {
      try {
        // Remove commas before parsing
        double number = Double.parseDouble(trimmed.replace(",", ""));
        isValid = number >= 0;
      } catch (NumberFormatException e) {
        isValid = false;
      }
    }

    if (isValid) {
      Logger.success("Numeric value is valid.", false);
    } else {
      Logger.error("Invalid numeric value.", false);
    }

    return isValid;
  }

  public static boolean validatePayslipRequest(
      Component parent,
      String inputId,
      boolean restrictToLoggedInEmployee,
      String loggedInUserUsername,
      String[] matchedRecord) {
    if (inputId == null || inputId.trim().isEmpty()) {
      Logger.error("Please enter an Employee ID before opening the payslip.", true);
      return false;
    }

    if (restrictToLoggedInEmployee && !inputId.equalsIgnoreCase(loggedInUserUsername)) {
      Logger.error(
          "Error: You are not authorized to view other employee records. Please use your own Employee ID.",
          true);
      return false;
    }

    if (matchedRecord == null) {
      Logger.error(
          "Database Fault: Selected Employee ID record path is currently missing inside system arrays.",
          true);
      return false;
    }

    Logger.success("Payslip request validated successfully.", false);
    return true;
  }

  private static boolean isRequiredFieldFilled(String value) {
    return value != null && !value.trim().isEmpty() && !"MM/DD/YYYY".equalsIgnoreCase(value.trim());
  }

  public static String collectEmployeeValidationErrors(
      String lastName,
      String firstName,
      String birthday,
      String phoneNumber,
      String sssNumber,
      String philHealthNumber,
      String tinNumber,
      String pagIbigNumber,
      String basicSalary,
      String riceSubsidy,
      String phoneAllowance,
      String clothingAllowance,
      String grossSemiMonthly,
      String hourlyRate) {
    List<String> errors = new ArrayList<>();

    if (!isRequiredFieldFilled(lastName)) {
      errors.add("Last Name is required.");
    }
    if (!isRequiredFieldFilled(firstName)) {
      errors.add("First Name is required.");
    }
    if (!isRequiredFieldFilled(birthday) || !isValidBirthDate(birthday)) {
      errors.add("Birthday must use MM/DD/YYYY format.");
    }
    if (!isValidPhoneNumber(phoneNumber)) {
      errors.add("Phone Number must contain only digits (10-15 digits).");
    }
    if (!isRequiredFieldFilled(sssNumber) || !isValidSss(sssNumber)) {
      errors.add("SSS Number must follow the XX-XXXXXXX-X format.");
    }
    if (!isRequiredFieldFilled(philHealthNumber) || !isValidPhilHealth(philHealthNumber)) {
      errors.add("PhilHealth Number must contain 12 digits.");
    }
    if (!isRequiredFieldFilled(tinNumber) || !isValidTin(tinNumber)) {
      errors.add("TIN must follow the XXX-XXX-XXX-XXX format.");
    }
    if (!isRequiredFieldFilled(pagIbigNumber) || !isValidPagIbig(pagIbigNumber)) {
      errors.add("Pag-IBIG Number must contain 12 digits.");
    }
    if (!isRequiredFieldFilled(basicSalary) || !isValidNumericValue(basicSalary)) {
      errors.add("Basic Salary must be a non-negative number.");
    }
    if (!isValidNumericValue(riceSubsidy)) {
      errors.add("Rice Subsidy must be a non-negative number.");
    }
    if (!isValidNumericValue(phoneAllowance)) {
      errors.add("Phone Allowance must be a non-negative number.");
    }
    if (!isValidNumericValue(clothingAllowance)) {
      errors.add("Clothing Allowance must be a non-negative number.");
    }
    if (!isValidNumericValue(grossSemiMonthly)) {
      errors.add("Gross Semi-Monthly Rate must be a non-negative number.");
    }
    if (!isRequiredFieldFilled(hourlyRate) || !isValidNumericValue(hourlyRate)) {
      errors.add("Hourly Rate must be a non-negative number.");
    }

    if (errors.isEmpty()) {
      Logger.success("Employee input validation passed.", false);
      return null;
    }

    Logger.error("Employee input validation failed.", false);
    return String.join(System.lineSeparator(), errors);
  }

  public static boolean validateEmployeeInput(
      Component parent,
      String lastName,
      String firstName,
      String birthday,
      String phoneNumber,
      String sssNumber,
      String philHealthNumber,
      String tinNumber,
      String pagIbigNumber,
      String basicSalary,
      String riceSubsidy,
      String phoneAllowance,
      String clothingAllowance,
      String grossSemiMonthly,
      String hourlyRate) {
    String validationMessage =
        collectEmployeeValidationErrors(
            lastName,
            firstName,
            birthday,
            phoneNumber,
            sssNumber,
            philHealthNumber,
            tinNumber,
            pagIbigNumber,
            basicSalary,
            riceSubsidy,
            phoneAllowance,
            clothingAllowance,
            grossSemiMonthly,
            hourlyRate);

    if (validationMessage != null) {
      Logger.error("Validation Error:\n\n" + validationMessage, true);
    }

    return validationMessage == null;
  }

  private static boolean isBlankOrPlaceholder(String value) {
    if (value == null) {
      Logger.success("Input was left blank.", false);
      return true;
    }

    String trimmed = value.trim();
    if (trimmed.isEmpty() || "MM/DD/YYYY".equalsIgnoreCase(trimmed)) {
      Logger.success("Input was left blank or used a placeholder.", false);
      return true;
    }

    return false;
  }
}
