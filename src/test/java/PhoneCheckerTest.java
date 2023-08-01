import lombok.val;
import org.example.antispamgorbushka.service.PhoneChecker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PhoneCheckerTest {

    @Test
    public void containtPhoneTest() {
        String[] validPhoneNumbers
//                = {"fdsgd 2055550125 dfsgsdfg", " dsfg 202 555 0125 dsfgsdfg", " dsfg (202) 555-0125 dsfg ", " dsfg +111 (202) 555-0125 dsfg ",
//                " dsfg 636 856 789 dsfg ", " dsfg +111 636 856 789 dsfg ", " dsfg 636 85 67 89 dsfg ", " dsfg +111 636 85 67 89 dsfg "
//                , " dsfg 89171688704 dsfg "
//                , "+7(917)168-04"
//                };
                = {
                "89171688704"
                , "+79171688704"
                , "8-917-168-87-04"
                , "+7(917)-168-87-04"
        };
        val phoneChecker = new PhoneChecker();

        for (String phoneNumber : validPhoneNumbers) {
            assertTrue(phoneChecker.containtPhone(phoneNumber), phoneNumber);
        }
    }
}
