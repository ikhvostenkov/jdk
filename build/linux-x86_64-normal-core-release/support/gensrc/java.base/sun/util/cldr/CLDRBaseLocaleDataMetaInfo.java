/*
 * Copyright (c) 2012, 2021, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package sun.util.cldr;

import java.util.HashMap;
import java.util.Map;
import java.util.ListResourceBundle;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleDataMetaInfo;

public class CLDRBaseLocaleDataMetaInfo extends ListResourceBundle implements LocaleDataMetaInfo {
    @Override
    protected final Object[][] getContents() {
        final Object[][] data = new Object[][] {
            { "CurrencyNames",
              "en en-001" },
            { "AvailableLocales",
              "en en-001 en-150 en-US en-US-POSIX" },
            { "parentLocale.es-419",
              "es-AR es-BO es-CL es-CO es-CR es-CU es-DO es-EC es-GT es-HN es-MX es-NI es-PA es-PE es-PR es-PY es-SV es-US es-UY es-VE" },
            { "CalendarData",
              "en-US" },
            { "FormatData",
              "en en-001 en-150 en-US en-US-POSIX" },
            { "LocaleNames",
              "en" },
            { "TimeZoneNames",
              "en en-001" },
            { "parentLocale.root",
              "az-Cyrl bm-Nkoo bs-Cyrl en-Dsrt en-Shaw ha-Arab iu-Latn mn-Mong ms-Arab pa-Arab shi-Latn sr-Latn uz-Arab uz-Cyrl vai-Latn zh-Hant" },
            { "parentLocale.en-001",
              "en-150 en-AG en-AI en-AU en-BB en-BE en-BM en-BS en-BW en-BZ en-CA en-CC en-CK en-CM en-CX en-DG en-DM en-ER en-FJ en-FK en-FM en-GB en-GD en-GG en-GH en-GI en-GM en-GY en-HK en-IE en-IM en-IN en-IO en-JE en-JM en-KE en-KI en-KN en-KY en-LC en-LR en-LS en-MG en-MO en-MS en-MT en-MU en-MW en-MY en-NA en-NF en-NG en-NR en-NU en-NZ en-PG en-PH en-PK en-PN en-PW en-RW en-SB en-SC en-SD en-SG en-SH en-SL en-SS en-SX en-SZ en-TC en-TK en-TO en-TT en-TV en-TZ en-UG en-VC en-VG en-VU en-WS en-ZA en-ZM en-ZW" },
            { "parentLocale.zh-Hant-HK",
              "zh-Hant-MO" },
            { "parentLocale.pt-PT",
              "pt-AO pt-CV pt-GW pt-MO pt-MZ pt-ST pt-TL" },
        };
        return data;
    }


    @Override
    public LocaleProviderAdapter.Type getType() {
        return LocaleProviderAdapter.Type.CLDR;
    }


    @Override
    public String availableLanguageTags(String category) {
        return getString(category);
    };


    public Map<String, String> parentLocales() {
        Map<String, String> ret = new HashMap<>();
        keySet().stream()
            .filter(key -> key.startsWith("parentLocale."))
            .forEach(key -> ret.put(key.substring(13), getString(key)));
        return ret.isEmpty() ? null : ret;
    };
}