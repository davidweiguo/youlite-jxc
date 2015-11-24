package com.youlite.jxc.server.keeper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.youlite.jxc.common.util.DualKeyMap;
import com.youlite.jxc.server.pojo.Goods;
import com.youlite.jxc.server.pojo.GoodsIn;
import com.youlite.jxc.server.pojo.GoodsOut;
import com.youlite.jxc.server.pojo.GoodsReturn;

public class GoodsKeeper {
	// k=Goods id; v=Goods
	private Map<String, Goods> goodsMap = new ConcurrentHashMap<String, Goods>();

	// k1=GoodsIn id; k2=Goods id; v=GoodsIn
	private DualKeyMap<String, String, GoodsIn> goodsInMap = new DualKeyMap<String, String, GoodsIn>();

	// k1=GoodsOut id; k2=Goods id; v=GoodsOut
	private DualKeyMap<String, String, GoodsOut> goodsOutMap = new DualKeyMap<String, String, GoodsOut>();

	// k1=GoodsReturn id; k2=GoodsOut id; v=GoodsReturn
	private DualKeyMap<String, String, GoodsReturn> goodsReturnMap = new DualKeyMap<String, String, GoodsReturn>();

	// k1=User id; k2=Goods id; v=Goods
	private Map<String, Map<String, Goods>> userGoodsMap = new ConcurrentHashMap<String, Map<String, Goods>>();

	public void injectGoods(List<Goods> goods) {
		for (Goods goodsTemp : goods) {
			goodsMap.put(goodsTemp.getId(), goodsTemp);
		}
	}

	public void injectGoodsIns(List<GoodsIn> goodsIns) {
		for (GoodsIn goodsIn : goodsIns) {
			goodsInMap
					.put(goodsIn.getId(), goodsIn.getGoods().getId(), goodsIn);
			Map<String, Goods> goodsMap = null;
			if (!userGoodsMap.containsKey(goodsIn.getUserId())) {
				goodsMap = new ConcurrentHashMap<String, Goods>();
				userGoodsMap.put(goodsIn.getUserId(), goodsMap);
			} else {
				goodsMap = userGoodsMap.get(goodsIn.getUserId());
			}
			if (!goodsMap.containsKey(goodsIn.getGoods().getId())) {
				goodsMap.put(goodsIn.getGoods().getId(), goodsIn.getGoods());
			}
		}
	}

	public void injectGoodsOuts(List<GoodsOut> goodsOuts) {
		for (GoodsOut goodsOut : goodsOuts) {
			goodsOutMap.put(goodsOut.getId(), goodsOut.getGoods().getId(),
					goodsOut);
			Map<String, Goods> goodsMap = null;
			if (!userGoodsMap.containsKey(goodsOut.getUserId())) {
				goodsMap = new ConcurrentHashMap<String, Goods>();
				userGoodsMap.put(goodsOut.getUserId(), goodsMap);
			} else {
				goodsMap = userGoodsMap.get(goodsOut.getUserId());
			}
			if (!goodsMap.containsKey(goodsOut.getGoods().getId())) {
				goodsMap.put(goodsOut.getGoods().getId(), goodsOut.getGoods());
			}
		}
	}

	public void injectGoodsReturns(List<GoodsReturn> goodsReturns) {
		for (GoodsReturn goodsReturn : goodsReturns) {
			goodsReturnMap.put(goodsReturn.getId(),
					goodsReturn.getGoodsOutId(), goodsReturn);
		}
	}
}
