# url_config.py

"""
크롤링할 URL, 카테고리, 스토어 정보를 한 곳에 모아두는 파일
"""

URL_TASKS = [
    
    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/8072778306?&trTypeCd=MAS51&trCtgrNo=585021&checkCtlgPrd=true"
    },
    
    ##
    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000671065203&siteNo=6004&salestrNo=6005"
    },
    ##
    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000671882935&siteNo=6004&salestrNo=6005"
    },
    ##
    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000671547643&siteNo=7024&salestrNo=6005"
    },
    ##
    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/8036671828?&trTypeCd=MAS51&trCtgrNo=585021&checkCtlgPrd=true"
    },
    ##
    
    {
        "categoryName": "IntelCPU",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/7917239088?&trTypeCd=PW00&trCtgrNo=585021&checkCtlgPrd=true"
    },
    {
        "categoryName": "IntelCPU",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/6634754435?&trTypeCd=MAS101&trCtgrNo=585021&checkCtlgPrd=true"
    },
    {
        "categoryName": "IntelCPU",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/4067321598?&trTypeCd=MAS101&trCtgrNo=585021&checkCtlgPrd=true"
    },
    
    # 3060 ti
    {
        "categoryName": "GraphicsCard",
        "storeName": "Amazon",
        "url": "https://www.amazon.com/Zotac-Gaming-GeForce-NVIDIA-GDDR6X/dp/B0BN4DKMQC"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000321766163"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/7247637565?&trTypeCd=PW00&trCtgrNo=585021&checkCtlgPrd=true"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000575711752&siteNo=6004&salestrNo=6005"
    },

    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000669358487&siteNo=7024&salestrNo=6005"
    },

    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000669358482&siteNo=7024&salestrNo=6005"
    },

    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/4022897205?&trTypeCd=MAS51&trCtgrNo=585021&checkCtlgPrd=true"
    },

    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/7916943267?&trTypeCd=PW00&trCtgrNo=585021&checkCtlgPrd=true"
    },

    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/8050577890?&trTypeCd=MAS51&trCtgrNo=585021&checkCtlgPrd=true"
    },

    ### 4060ti
    {
        "categoryName": "GraphicsCard",
        "storeName": "Amazon",
        "url": "https://www.amazon.com/ASUS-GeForce-Gaming-Graphics-DisplayPort/dp/B0C5SDY831/ref=sr_1_5?crid=3MO0KFNFTPHTN&dib=eyJ2IjoiMSJ9.8Lp_JpkmiW7qch8_ecnVtBthWPClaOIiOgbWKVCcHue0g3qOHnhDX7fI0UJrh2UNqir0RURUDvMNm8HkcBRe4LI4rMsVsOHKAo9JehBn9H6wAca0Rp47Y_8dkCtnRvjRugmcDIdQdqvvR4RMKrv4mI1EZxocgJXqPK-WkOIvMPELoFIXOzCQlhq0bxkfyD-bvBa_LL7tqq8qicffHxwM12iV5ZtvKc0gte6S4Oj94ec.BsCZE3smbYF75nUnm0eR9Vu4HQ8t4YhiObdlyuyQCJc&dib_tag=se&keywords=4060ti&qid=1741574301&sprefix=060ti%2Caps%2C421&sr=8-5&th=1"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "Amazon",
        "url": "https://www.amazon.com/INNO3D-Geforce-4060-Twin-Grafikkarte/dp/B0C67QD6B2/ref=sr_1_7?crid=1QQZ6ZPCNVQZ8&dib=eyJ2IjoiMSJ9.8Lp_JpkmiW7qch8_ecnVtBthWPClaOIiOgbWKVCcHue0g3qOHnhDX7fI0UJrh2UNqir0RURUDvMNm8HkcBRe4LI4rMsVsOHKAo9JehBn9H6wAca0Rp47Y_8dkCtnRvjRugmcDIdQdqvvR4RMKrv4mI1EZxocgJXqPK-WkOIvMPELoFIXOzCQlhq0bxkfyD-bvBa_LL7tqq8qicffHxwM12iV5ZtvKc0gte6S4Oj94ec.BsCZE3smbYF75nUnm0eR9Vu4HQ8t4YhiObdlyuyQCJc&dib_tag=se&keywords=4060ti&qid=1741574413&sprefix=4060ti%2Caps%2C409&sr=8-7"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "Amazon",
        "url": "https://www.amazon.com/ZOTAC-Gaming-RTX4060-Twin-Edge/dp/B0C7178S9K/ref=sr_1_2?content-id=amzn1.sym.5aa19d53-77d3-406e-a166-ae5e47edb27b%3Aamzn1.sym.5aa19d53-77d3-406e-a166-ae5e47edb27b&dib=eyJ2IjoiMSJ9.dsqDSKbuUef5vLM4G_8ZOFik7f601Sb11XGHpU30GltbQ5ZsW3ESGMarvoUJBoLOSd6RcZCk_bPkiFsCqcsyMHFCH-FTkrys1KpQa34CTbhK0k7SR7wwElJm6rzdXiUytE8SEAOvGN7S3Qn2_o4MG_yC5sOUmAOvpz4yrPvWRvyGI_0iLK6v5yrvAWZSIdmqOyCZfKUGjsuCiwwKnHNc5e57OjRmLJOTx696bvNfa1Y._SPWI8z-rjZb0eSpBeMkEc_qydBgN4jthjs4pBMz2Ok&dib_tag=se&keywords=4060ti+16gb&pd_rd_r=080723b8-2708-4cd4-9c5f-f69a32e77d59&pd_rd_w=kUpjH&pd_rd_wg=8fPEW&qid=1741574468&sr=8-2"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/7958256765?&trTypeCd=PW00&trCtgrNo=585021&checkCtlgPrd=true"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/7072959724?&trTypeCd=PW24&trCtgrNo=585021"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/8021730328?&trTypeCd=MAS51&trCtgrNo=585021&checkCtlgPrd=true"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/8040200958?&trTypeCd=MAS51&trCtgrNo=585021&checkCtlgPrd=true"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/5843593550?&trTypeCd=MAS51&trCtgrNo=585021&checkCtlgPrd=true"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/7848265710?&trTypeCd=MAS77&trCtgrNo=585021&checkCtlgPrd=true"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000632818219&siteNo=6004&salestrNo=6005&advertBidId=9999999998"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000632343050&siteNo=6004&salestrNo=6005"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000649017849&siteNo=7024&salestrNo=6005"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000630021024&siteNo=6004&salestrNo=6005"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000553898033&siteNo=6004&salestrNo=6005&advertBidId=9999999998"
    },

    # AMD 7000 & 8000 Series
    {"categoryName": "amdcpu", "storeName": "Amazon",
     "url": "https://www.amazon.com/AMD-Ryzen-8500G-12-Thread-Processor/dp/B0CQ4JV8D5/"},
    {"categoryName": "amdcpu", "storeName": "Amazon",
     "url": "https://www.amazon.com/AMD-Ryzen-8700G-16-Thread-Processor/dp/B0CQ4JBKW3/"},
    {"categoryName": "amdcpu", "storeName": "Amazon",
     "url": "https://www.amazon.com/AMD-RyzenTM-5-8400F/dp/B0D2JD6P86/"},
    {"categoryName": "amdcpu", "storeName": "Amazon",
     "url": "https://www.amazon.com/AMD-7600-12-Thread-Unlocked-Processor/dp/B0BMQJWBDM/"},
    {"categoryName": "amdcpu", "storeName": "Amazon",
     "url": "https://www.amazon.com/AMD-7700X-16-Thread-Unlocked-Processor/dp/B0BBHHT8LY/"},
    {"categoryName": "amdcpu", "storeName": "Amazon",
     "url": "https://www.amazon.com/AMD-7900X-24-Thread-Unlocked-Processor/dp/B0BBJ59WJ4/"},
    {"categoryName": "amdcpu", "storeName": "Amazon",
     "url": "https://www.amazon.com/AMD-Ryzen-7800X3D-16-Thread-Processor/dp/B0BTZB7F88/"},

    {"categoryName": "amdcpu", "storeName": "SSG",
     "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000666746891&siteNo=7024&salestrNo=6005"},
    {"categoryName": "amdcpu", "storeName": "SSG",
     "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000666747868&siteNo=7024&salestrNo=6005"},
    {"categoryName": "amdcpu", "storeName": "SSG",
     "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000608252469&siteNo=6004&salestrNo=6005"},
    {"categoryName": "amdcpu", "storeName": "SSG",
     "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000666745764&siteNo=7024&salestrNo=6005"},

    {"categoryName": "amdcpu", "storeName": "11ST",
     "url": "https://www.11st.co.kr/products/7327357512?&trTypeCd=MAS101&trCtgrNo=585021&checkCtlgPrd=true"},

    ## 4070ti
    {
        "categoryName": "GraphicsCard",
        "storeName": "Amazon",
        "url": "https://www.amazon.com/Gainward-RTX4070Ti-Phantom-Reunion-12GB/dp/B0BRXZ63PV/ref=sr_1_1?crid=1INTDK5VNYPD1&dib=eyJ2IjoiMSJ9.BQgNPfyImA3w5qECClhhAljI5kHcmwrLUHgiFWbEGLcmOvmamPyuvE0tG1pzVh6uE470h3gLTUw80yMYKvQH1ZUCo_japV0i9lD2JSqKXKjTJBDewqQVp16XFi1Z2p9SdIZcBlHW1DgGSMh9eD34ZzU-27w1ztgsdn5Ip0HvV48dJLBGGO5f6d7_OTga7nZU1zYkakS-arU-LNHAlazM-LFGx06PcS4o6EdREurqwqU.O8tH-HJEqj0NLBJHOAK4JmY4RaTcKqndth9oFne7Erw&dib_tag=se&keywords=4070ti&qid=1741590134&sprefix=4070ti%2Caps%2C345&sr=8-1"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "Amazon",
        "url": "https://www.amazon.com/PNY-VCG4070TS16TFXXPB1-16GB-GDDR6X-Graphics/dp/B0D2J4DCW1/ref=sr_1_2?crid=1INTDK5VNYPD1&dib=eyJ2IjoiMSJ9.BQgNPfyImA3w5qECClhhAljI5kHcmwrLUHgiFWbEGLcmOvmamPyuvE0tG1pzVh6uE470h3gLTUw80yMYKvQH1ZUCo_japV0i9lD2JSqKXKjTJBDewqQVp16XFi1Z2p9SdIZcBlHW1DgGSMh9eD34ZzU-27w1ztgsdn5Ip0HvV48dJLBGGO5f6d7_OTga7nZU1zYkakS-arU-LNHAlazM-LFGx06PcS4o6EdREurqwqU.O8tH-HJEqj0NLBJHOAK4JmY4RaTcKqndth9oFne7Erw&dib_tag=se&keywords=4070ti&qid=1741590134&sprefix=4070ti%2Caps%2C345&sr=8-2"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "Amazon",
        "url": "https://www.amazon.com/ASUS-ProArt-GeForce-Graphics-DisplayPort/dp/B0C6FXM199/ref=sr_1_4?crid=1INTDK5VNYPD1&dib=eyJ2IjoiMSJ9.BQgNPfyImA3w5qECClhhAljI5kHcmwrLUHgiFWbEGLcmOvmamPyuvE0tG1pzVh6uE470h3gLTUw80yMYKvQH1ZUCo_japV0i9lD2JSqKXKjTJBDewqQVp16XFi1Z2p9SdIZcBlHW1DgGSMh9eD34ZzU-27w1ztgsdn5Ip0HvV48dJLBGGO5f6d7_OTga7nZU1zYkakS-arU-LNHAlazM-LFGx06PcS4o6EdREurqwqU.O8tH-HJEqj0NLBJHOAK4JmY4RaTcKqndth9oFne7Erw&dib_tag=se&keywords=4070ti&qid=1741590134&sprefix=4070ti%2Caps%2C345&sr=8-4"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "Amazon",
        "url": "https://www.amazon.com/MSI-Gaming-Graphics-NVIDIA-256-Bit/dp/B0CWS78Y5J/ref=sr_1_6?crid=1INTDK5VNYPD1&dib=eyJ2IjoiMSJ9.BQgNPfyImA3w5qECClhhAljI5kHcmwrLUHgiFWbEGLcmOvmamPyuvE0tG1pzVh6uE470h3gLTUw80yMYKvQH1ZUCo_japV0i9lD2JSqKXKjTJBDewqQVp16XFi1Z2p9SdIZcBlHW1DgGSMh9eD34ZzU-27w1ztgsdn5Ip0HvV48dJLBGGO5f6d7_OTga7nZU1zYkakS-arU-LNHAlazM-LFGx06PcS4o6EdREurqwqU.O8tH-HJEqj0NLBJHOAK4JmY4RaTcKqndth9oFne7Erw&dib_tag=se&keywords=4070ti&qid=1741590134&sprefix=4070ti%2Caps%2C345&sr=8-6"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/7958287486?&trTypeCd=PW24&trCtgrNo=585021"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/7958318835?&trTypeCd=PW24&trCtgrNo=585021"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/7958223000?&trTypeCd=PW24&trCtgrNo=585021"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/7906027595?&trTypeCd=PW24&trCtgrNo=585021"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/7955330977?&trTypeCd=PW24&trCtgrNo=585021"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/7936564260?&trTypeCd=PW24&trCtgrNo=585021"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000597656603&siteNo=6004&salestrNo=6005"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000577026700&siteNo=6004&salestrNo=6005"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000610824710&siteNo=6004&salestrNo=6005"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000640022631&siteNo=7024&salestrNo=6005"
    },
    
    # ----------------------------------------------------
    # Intel CPU (Amazon)
    # ----------------------------------------------------
    {
        "categoryName": "IntelCPU",
        "storeName": "Amazon",
        "url": "https://www.amazon.com/i9-14900K-Desktop-Processor-Integrated-Graphics/dp/B0CGJDKLB8/ref=sr_1_1?crid=9JV0PDEE0YN4&dib=eyJ2IjoiMSJ9.lW0i6XHxFLVDE3_MzhtVDwHsZKSLGlNvTomtFni-Z_aZtIIWUOR86P_5FfQ7mEYTPzRcBVcOzODnQB5JRIeWxwArp_k2AzqGO4PM5mB5KX_liDkvhObKczdvQX29552b_w7IbMWAUujx_dAq9DM53PiQpFTVsPmBSPDhFlEmiov2DFVIwOdD91gjNPcZVct0P1knGYJ8v51tWaJbqzMJcr0wf0hAWSP3Z1HnHH_Tupk.hOLJHEsM82mT08P4gehxDDwAHPaeQ95sene7Qn-0_cc&dib_tag=se&keywords=intel%2Bcpu&qid=1741738959&sprefix=intel%2Bcp%2Caps%2C328&sr=8-1&th=1"
    },
    {
        "categoryName": "IntelCPU",
        "storeName": "Amazon",
        "url": "https://www.amazon.com/Intel-i7-12700KF-Desktop-Processor-Unlocked/dp/B09FXKHN7M/ref=sr_1_2?crid=9JV0PDEE0YN4&dib=eyJ2IjoiMSJ9.lW0i6XHxFLVDE3_MzhtVDwHsZKSLGlNvTomtFni-Z_aZtIIWUOR86P_5FfQ7mEYTPzRcBVcOzODnQB5JRIeWxwArp_k2AzqGO4PM5mB5KX_liDkvhObKczdvQX29552b_w7IbMWAUujx_dAq9DM53PiQpFTVsPmBSPDhFlEmiov2DFVIwOdD91gjNPcZVct0P1knGYJ8v51tWaJbqzMJcr0wf0hAWSP3Z1HnHH_Tupk.hOLJHEsM82mT08P4gehxDDwAHPaeQ95sene7Qn-0_cc&dib_tag=se&keywords=intel+cpu&qid=1741738959&sprefix=intel+cp%2Caps%2C328&sr=8-2"
    },
    {
        "categoryName": "IntelCPU",
        "storeName": "Amazon",
        "url": "https://www.amazon.com/Intel-i7-12700K-Desktop-Processor-Unlocked/dp/B09FXNVDBJ/ref=sr_1_3?crid=9JV0PDEE0YN4&dib=eyJ2IjoiMSJ9.lW0i6XHxFLVDE3_MzhtVDwHsZKSLGlNvTomtFni-Z_aZtIIWUOR86P_5FfQ7mEYTPzRcBVcOzODnQB5JRIeWxwArp_k2AzqGO4PM5mB5KX_liDkvhObKczdvQX29552b_w7IbMWAUujx_dAq9DM53PiQpFTVsPmBSPDhFlEmiov2DFVIwOdD91gjNPcZVct0P1knGYJ8v51tWaJbqzMJcr0wf0hAWSP3Z1HnHH_Tupk.hOLJHEsM82mT08P4gehxDDwAHPaeQ95sene7Qn-0_cc&dib_tag=se&keywords=intel+cpu&qid=1741738959&sprefix=intel+cp%2Caps%2C328&sr=8-3"
    },
    {
        "categoryName": "IntelCPU",
        "storeName": "Amazon",
        "url": "https://www.amazon.com/Intel-12400F-Desktop-Processor-Cache/dp/B09NPJRDGD/ref=sr_1_5?crid=9JV0PDEE0YN4&dib=eyJ2IjoiMSJ9.lW0i6XHxFLVDE3_MzhtVDwHsZKSLGlNvTomtFni-Z_aZtIIWUOR86P_5FfQ7mEYTPzRcBVcOzODnQB5JRIeWxwArp_k2AzqGO4PM5mB5KX_liDkvhObKczdvQX29552b_w7IbMWAUujx_dAq9DM53PiQpFTVsPmBSPDhFlEmiov2DFVIwOdD91gjNPcZVct0P1knGYJ8v51tWaJbqzMJcr0wf0hAWSP3Z1HnHH_Tupk.hOLJHEsM82mT08P4gehxDDwAHPaeQ95sene7Qn-0_cc&dib_tag=se&keywords=intel+cpu&qid=1741738959&sprefix=intel+cp%2Caps%2C328&sr=8-5"
    },

    # ----------------------------------------------------
    # Intel CPU (SSG)
    # ----------------------------------------------------
    {
        "categoryName": "IntelCPU",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000582043443&siteNo=6001&salestrNo=6005"
    },
    {
        "categoryName": "IntelCPU",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000580761861&siteNo=6004&salestrNo=6005"
    },
    {
        "categoryName": "IntelCPU",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000582043452&siteNo=6001&salestrNo=6005"
    },
    {
        "categoryName": "IntelCPU",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000582043459&siteNo=6001&salestrNo=6005"
    },
    {
        "categoryName": "IntelCPU",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000629408306&siteNo=6004&salestrNo=6005"
    },
    {
        "categoryName": "IntelCPU",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000624698547&siteNo=6004&salestrNo=6005"
    },
    {
        "categoryName": "IntelCPU",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000624698535&siteNo=6004&salestrNo=6005"
    },

    # ----------------------------------------------------
    # Intel CPU (11ST)
    # ----------------------------------------------------
    

    # ----------------------------------------------------
    # 5070 Ti (SSG & 11ST)
    # ----------------------------------------------------
    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000669545657&siteNo=6004&salestrNo=6005&advertBidId=9999999998"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000671655268&siteNo=6004&salestrNo=6005"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "SSG",
        "url": "https://www.ssg.com/item/itemView.ssg?itemId=1000665620830&siteNo=7024&salestrNo=6005"
    },
    
    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/8040918455?&trTypeCd=MAS51&trCtgrNo=585021&checkCtlgPrd=true"
    },
    {
        "categoryName": "GraphicsCard",
        "storeName": "11ST",
        "url": "https://www.11st.co.kr/products/8064748690?&trTypeCd=MAS51&trCtgrNo=585021&checkCtlgPrd=true"
    }
]
