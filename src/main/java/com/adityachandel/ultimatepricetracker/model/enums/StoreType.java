package com.adityachandel.ultimatepricetracker.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StoreType {
    AMAZON(
            "AMAZON",
            "0 */30 * ? * *",
            "Amazon",
            false,
            false,
            "https://cdn.iconscout.com/icon/free/png-512/free-amazon-43-432492.png"
    ),
    MACYS(
            "MACYS",
            "0 0 0/6 ? * * *",
            "Macy's",
            true,
            true,
            "https://cdn.iconscout.com/icon/free/png-512/free-macys-3628899-3030039.png"
    ),
    ANN_TAYLOR(
            "ANN_TAYLOR",
            "0 0 3/6 ? * * *",
            "Ann Taylor",
            true,
            true,
            "https://cdn.iconscout.com/icon/free/png-512/free-dress-3832749-3192592.png"
    ),
    MANGO(
            "MANGO",
            "0 0 6/6 ? * * *",
            "Mango",
            true,
            true,
            "https://cdn.iconscout.com/icon/free/png-512/free-mango-1624231-1375388.png"
    ),
    ANTHROPOLOGIE(
            "ANTHROPOLOGIE",
            "0 0 9/6 ? * * *",
            "Anthropologie",
            true,
            true,
            "https://res.cloudinary.com/crunchbase-production/image/upload/k4gyh3zlmwrxo1t3ioh6"
    );

    StoreType(String enumName, String defaultCron, String name, boolean supportsColor, boolean supportsSize, String iconUrl) {
        this.enumName = enumName;
        this.cron = defaultCron;
        this.name = name;
        this.supportsColor = supportsColor;
        this.supportsSize = supportsSize;
        this.iconUrl = iconUrl;
    }

    private final String enumName;
    private final String cron;
    private final String name;
    private final boolean supportsColor;
    private final boolean supportsSize;
    private final String iconUrl;

}
