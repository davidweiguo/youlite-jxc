package com.youlite.jxc.server.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.youlite.jxc.common.IPlugin;
import com.youlite.jxc.common.event.AsyncEventProcessor;
import com.youlite.jxc.common.event.IAsyncEventManager;
import com.youlite.jxc.common.event.IRemoteEventManager;
import com.youlite.jxc.server.keeper.GoodsKeeper;
import com.youlite.jxc.server.pojo.Goods;
import com.youlite.jxc.server.pojo.GoodsIn;
import com.youlite.jxc.server.pojo.GoodsOut;
import com.youlite.jxc.server.pojo.GoodsReturn;

public class GoodsManager implements IPlugin {

	private static final Logger log = LoggerFactory.getLogger(GoodsManager.class);
			
	@Autowired
	private IRemoteEventManager eventManager;

	@Autowired
	private GoodsKeeper goodsKeeper;

	private AsyncEventProcessor eventProcessor = new AsyncEventProcessor() {

		@Override
		public void subscribeToEvents() {
			// subscribeToEvent(NodeInfoEvent.class, null);
		}

		@Override
		public IAsyncEventManager getEventManager() {
			return eventManager;
		}

	};

	public void init() throws Exception {
		eventProcessor.setHandler(this);
		eventProcessor.init();
	}

	public void uninit() {
		eventProcessor.uninit();
	}

	public void injectGoods(List<Goods> goods) {
		goodsKeeper.injectGoods(goods);
	}

	public void injectGoodsIns(List<GoodsIn> goodsIns) {
		goodsKeeper.injectGoodsIns(goodsIns);
	}

	public void injectGoodsOuts(List<GoodsOut> goodsOuts) {
		goodsKeeper.injectGoodsOuts(goodsOuts);
	}

	public void injectGoodsReturns(List<GoodsReturn> goodsReturns) {
		goodsKeeper.injectGoodsReturns(goodsReturns);
	}
}
