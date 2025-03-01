/* ================================================
---------------------- Main.js ----------------- */

/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the W3C SOFTWARE AND DOCUMENT NOTICE AND LICENSE.
 *
 *  https://www.w3.org/Consortium/Legal/2015/copyright-software-and-document
 *
 */
!function () { "use strict"; if ( "object" == typeof window ) if ( "IntersectionObserver" in window && "IntersectionObserverEntry" in window && "intersectionRatio" in window.IntersectionObserverEntry.prototype ) "isIntersecting" in window.IntersectionObserverEntry.prototype || Object.defineProperty( window.IntersectionObserverEntry.prototype, "isIntersecting", { get: function () { return this.intersectionRatio > 0 } } ); else { var t = function ( t ) { for ( var e = window.document, o = i( e ); o; )o = i( e = o.ownerDocument ); return e }(), e = [], o = null, n = null; s.prototype.THROTTLE_TIMEOUT = 100, s.prototype.POLL_INTERVAL = null, s.prototype.USE_MUTATION_OBSERVER = !0, s._setupCrossOriginUpdater = function () { return o || ( o = function ( t, o ) { n = t && o ? l( t, o ) : { top: 0, bottom: 0, left: 0, right: 0, width: 0, height: 0 }, e.forEach( function ( t ) { t._checkForIntersections() } ) } ), o }, s._resetCrossOriginUpdater = function () { o = null, n = null }, s.prototype.observe = function ( t ) { if ( !this._observationTargets.some( function ( e ) { return e.element == t } ) ) { if ( !t || 1 != t.nodeType ) throw new Error( "target must be an Element" ); this._registerInstance(), this._observationTargets.push( { element: t, entry: null } ), this._monitorIntersections( t.ownerDocument ), this._checkForIntersections() } }, s.prototype.unobserve = function ( t ) { this._observationTargets = this._observationTargets.filter( function ( e ) { return e.element != t } ), this._unmonitorIntersections( t.ownerDocument ), 0 == this._observationTargets.length && this._unregisterInstance() }, s.prototype.disconnect = function () { this._observationTargets = [], this._unmonitorAllIntersections(), this._unregisterInstance() }, s.prototype.takeRecords = function () { var t = this._queuedEntries.slice(); return this._queuedEntries = [], t }, s.prototype._initThresholds = function ( t ) { var e = t || [ 0 ]; return Array.isArray( e ) || ( e = [ e ] ), e.sort().filter( function ( t, e, o ) { if ( "number" != typeof t || isNaN( t ) || t < 0 || t > 1 ) throw new Error( "threshold must be a number between 0 and 1 inclusively" ); return t !== o[ e - 1 ] } ) }, s.prototype._parseRootMargin = function ( t ) { var e = ( t || "0px" ).split( /\s+/ ).map( function ( t ) { var e = /^(-?\d*\.?\d+)(px|%)$/.exec( t ); if ( !e ) throw new Error( "rootMargin must be specified in pixels or percent" ); return { value: parseFloat( e[ 1 ] ), unit: e[ 2 ] } } ); return e[ 1 ] = e[ 1 ] || e[ 0 ], e[ 2 ] = e[ 2 ] || e[ 0 ], e[ 3 ] = e[ 3 ] || e[ 1 ], e }, s.prototype._monitorIntersections = function ( e ) { var o = e.defaultView; if ( o && -1 == this._monitoringDocuments.indexOf( e ) ) { var n = this._checkForIntersections, r = null, s = null; this.POLL_INTERVAL ? r = o.setInterval( n, this.POLL_INTERVAL ) : ( h( o, "resize", n, !0 ), h( e, "scroll", n, !0 ), this.USE_MUTATION_OBSERVER && "MutationObserver" in o && ( s = new o.MutationObserver( n ) ).observe( e, { attributes: !0, childList: !0, characterData: !0, subtree: !0 } ) ), this._monitoringDocuments.push( e ), this._monitoringUnsubscribes.push( function () { var t = e.defaultView; t && ( r && t.clearInterval( r ), c( t, "resize", n, !0 ) ), c( e, "scroll", n, !0 ), s && s.disconnect() } ); var u = this.root && ( this.root.ownerDocument || this.root ) || t; if ( e != u ) { var a = i( e ); a && this._monitorIntersections( a.ownerDocument ) } } }, s.prototype._unmonitorIntersections = function ( e ) { var o = this._monitoringDocuments.indexOf( e ); if ( -1 != o ) { var n = this.root && ( this.root.ownerDocument || this.root ) || t; if ( !this._observationTargets.some( function ( t ) { var o = t.element.ownerDocument; if ( o == e ) return !0; for ( ; o && o != n; ) { var r = i( o ); if ( ( o = r && r.ownerDocument ) == e ) return !0 } return !1 } ) ) { var r = this._monitoringUnsubscribes[ o ]; if ( this._monitoringDocuments.splice( o, 1 ), this._monitoringUnsubscribes.splice( o, 1 ), r(), e != n ) { var s = i( e ); s && this._unmonitorIntersections( s.ownerDocument ) } } } }, s.prototype._unmonitorAllIntersections = function () { var t = this._monitoringUnsubscribes.slice( 0 ); this._monitoringDocuments.length = 0, this._monitoringUnsubscribes.length = 0; for ( var e = 0; e < t.length; e++ )t[ e ]() }, s.prototype._checkForIntersections = function () { if ( this.root || !o || n ) { var t = this._rootIsInDom(), e = t ? this._getRootRect() : { top: 0, bottom: 0, left: 0, right: 0, width: 0, height: 0 }; this._observationTargets.forEach( function ( n ) { var i = n.element, s = u( i ), h = this._rootContainsTarget( i ), c = n.entry, a = t && h && this._computeTargetAndRootIntersection( i, s, e ), l = null; this._rootContainsTarget( i ) ? o && !this.root || ( l = e ) : l = { top: 0, bottom: 0, left: 0, right: 0, width: 0, height: 0 }; var f = n.entry = new r( { time: window.performance && performance.now && performance.now(), target: i, boundingClientRect: s, rootBounds: l, intersectionRect: a } ); c ? t && h ? this._hasCrossedThreshold( c, f ) && this._queuedEntries.push( f ) : c && c.isIntersecting && this._queuedEntries.push( f ) : this._queuedEntries.push( f ) }, this ), this._queuedEntries.length && this._callback( this.takeRecords(), this ) } }, s.prototype._computeTargetAndRootIntersection = function ( e, i, r ) { if ( "none" != window.getComputedStyle( e ).display ) { for ( var s, h, c, a, f, d, g, m, v = i, _ = p( e ), b = !1; !b && _; ) { var w = null, y = 1 == _.nodeType ? window.getComputedStyle( _ ) : {}; if ( "none" == y.display ) return null; if ( _ == this.root || 9 == _.nodeType ) if ( b = !0, _ == this.root || _ == t ) o && !this.root ? !n || 0 == n.width && 0 == n.height ? ( _ = null, w = null, v = null ) : w = n : w = r; else { var I = p( _ ), E = I && u( I ), T = I && this._computeTargetAndRootIntersection( I, E, r ); E && T ? ( _ = I, w = l( E, T ) ) : ( _ = null, v = null ) } else { var R = _.ownerDocument; _ != R.body && _ != R.documentElement && "visible" != y.overflow && ( w = u( _ ) ) } if ( w && ( s = w, h = v, c = void 0, a = void 0, f = void 0, d = void 0, g = void 0, m = void 0, c = Math.max( s.top, h.top ), a = Math.min( s.bottom, h.bottom ), f = Math.max( s.left, h.left ), d = Math.min( s.right, h.right ), m = a - c, v = ( g = d - f ) >= 0 && m >= 0 && { top: c, bottom: a, left: f, right: d, width: g, height: m } || null ), !v ) break; _ = _ && p( _ ) } return v } }, s.prototype._getRootRect = function () { var e; if ( this.root && !d( this.root ) ) e = u( this.root ); else { var o = d( this.root ) ? this.root : t, n = o.documentElement, i = o.body; e = { top: 0, left: 0, right: n.clientWidth || i.clientWidth, width: n.clientWidth || i.clientWidth, bottom: n.clientHeight || i.clientHeight, height: n.clientHeight || i.clientHeight } } return this._expandRectByRootMargin( e ) }, s.prototype._expandRectByRootMargin = function ( t ) { var e = this._rootMarginValues.map( function ( e, o ) { return "px" == e.unit ? e.value : e.value * ( o % 2 ? t.width : t.height ) / 100 } ), o = { top: t.top - e[ 0 ], right: t.right + e[ 1 ], bottom: t.bottom + e[ 2 ], left: t.left - e[ 3 ] }; return o.width = o.right - o.left, o.height = o.bottom - o.top, o }, s.prototype._hasCrossedThreshold = function ( t, e ) { var o = t && t.isIntersecting ? t.intersectionRatio || 0 : -1, n = e.isIntersecting ? e.intersectionRatio || 0 : -1; if ( o !== n ) for ( var i = 0; i < this.thresholds.length; i++ ) { var r = this.thresholds[ i ]; if ( r == o || r == n || r < o != r < n ) return !0 } }, s.prototype._rootIsInDom = function () { return !this.root || f( t, this.root ) }, s.prototype._rootContainsTarget = function ( e ) { var o = this.root && ( this.root.ownerDocument || this.root ) || t; return f( o, e ) && ( !this.root || o == e.ownerDocument ) }, s.prototype._registerInstance = function () { e.indexOf( this ) < 0 && e.push( this ) }, s.prototype._unregisterInstance = function () { var t = e.indexOf( this ); -1 != t && e.splice( t, 1 ) }, window.IntersectionObserver = s, window.IntersectionObserverEntry = r } function i( t ) { try { return t.defaultView && t.defaultView.frameElement || null } catch ( t ) { return null } } function r( t ) { this.time = t.time, this.target = t.target, this.rootBounds = a( t.rootBounds ), this.boundingClientRect = a( t.boundingClientRect ), this.intersectionRect = a( t.intersectionRect || { top: 0, bottom: 0, left: 0, right: 0, width: 0, height: 0 } ), this.isIntersecting = !!t.intersectionRect; var e = this.boundingClientRect, o = e.width * e.height, n = this.intersectionRect, i = n.width * n.height; this.intersectionRatio = o ? Number( ( i / o ).toFixed( 4 ) ) : this.isIntersecting ? 1 : 0 } function s( t, e ) { var o, n, i, r = e || {}; if ( "function" != typeof t ) throw new Error( "callback must be a function" ); if ( r.root && 1 != r.root.nodeType && 9 != r.root.nodeType ) throw new Error( "root must be a Document or Element" ); this._checkForIntersections = ( o = this._checkForIntersections.bind( this ), n = this.THROTTLE_TIMEOUT, i = null, function () { i || ( i = setTimeout( function () { o(), i = null }, n ) ) } ), this._callback = t, this._observationTargets = [], this._queuedEntries = [], this._rootMarginValues = this._parseRootMargin( r.rootMargin ), this.thresholds = this._initThresholds( r.threshold ), this.root = r.root || null, this.rootMargin = this._rootMarginValues.map( function ( t ) { return t.value + t.unit } ).join( " " ), this._monitoringDocuments = [], this._monitoringUnsubscribes = [] } function h( t, e, o, n ) { "function" == typeof t.addEventListener ? t.addEventListener( e, o, n || !1 ) : "function" == typeof t.attachEvent && t.attachEvent( "on" + e, o ) } function c( t, e, o, n ) { "function" == typeof t.removeEventListener ? t.removeEventListener( e, o, n || !1 ) : "function" == typeof t.detatchEvent && t.detatchEvent( "on" + e, o ) } function u( t ) { var e; try { e = t.getBoundingClientRect() } catch ( t ) { } return e ? ( e.width && e.height || ( e = { top: e.top, right: e.right, bottom: e.bottom, left: e.left, width: e.right - e.left, height: e.bottom - e.top } ), e ) : { top: 0, bottom: 0, left: 0, right: 0, width: 0, height: 0 } } function a( t ) { return !t || "x" in t ? t : { top: t.top, y: t.top, bottom: t.bottom, left: t.left, x: t.left, right: t.right, width: t.width, height: t.height } } function l( t, e ) { var o = e.top - t.top, n = e.left - t.left; return { top: o, left: n, height: e.height, width: e.width, bottom: o + e.height, right: n + e.width } } function f( t, e ) { for ( var o = e; o; ) { if ( o == t ) return !0; o = p( o ) } return !1 } function p( e ) { var o = e.parentNode; return 9 == e.nodeType && e != t ? i( e ) : ( o && o.assignedSlot && ( o = o.assignedSlot.parentNode ), o && 11 == o.nodeType && o.host ? o.host : o ) } function d( t ) { return t && 9 === t.nodeType } }();


( function ( $ ) {
	'use strict';
	/* jQuery easing */
	$.extend( $.easing, {
		def: 'easeOutQuad',
		swing: function ( x, t, b, c, d ) {
			return $.easing[ $.easing.def ]( x, t, b, c, d );
		},
		easeOutQuad: function ( x, t, b, c, d ) {
			return -c * ( t /= d ) * ( t - 2 ) + b;
		},
		easeOutQuint: function ( x, t, b, c, d ) {
			return c * ( ( t = t / d - 1 ) * t * t * t * t + 1 ) + b;
		}
	} );

	var Porto = {
		initialised: false,
		mobile: false,
		minipopup: {
			// info
			imageSrc: '',
			imageLink: '#',
			name: '',
			nameLink: '#', // 'product.html',
			content: 'has been added to your cart.',
			action: '<a href="cart.html" class="btn viewcart">View Cart</a><a href="checkout.html" class="btn btn-dark checkout">Checkout</a>',
			// option
			delay: 4000, // milliseconds
			space: 20,

			// template
			template: '<div class="minipopup-box">' +
				'<div class="product">' +
				'<figure class="product-media"><a href="{{imageLink}}"><img src="{{imageSrc}}" alt="product" width="60" height="60"></a></figure>' +
				'<div class="product-detail">' +
				'<a href="{{nameLink}}" class="product-name">{{name}}</a>' +
				'<p>{{content}}</p>' +
				'</div></div>' +
				'<div class="product-action">{{action}}</div>' +
				'<button class="mfp-close"></button></div>',
		},
		init: function () {
			if ( !this.initialised ) {
				this.initialised = true;
			} else {
				return;
			}

			// Call Porto Functions
			this.checkMobile();
			this.stickyHeader();
			this.headerSearchToggle();
			this.mMenuIcons();
			this.mMenuToggle();
			this.mobileMenu();
			this.scrollToTop();
			this.quantityInputs();
			this.alert();
			this.countTo();
			this.tooltip();
			this.popover();
			this.changePassToggle();
			this.changeBillToggle();
			this.catAccordion();
			this.toggleFilter();
			this.toggleSidebar();
			this.toggleCart();
			this.linkToTab();
			this.productTabSroll();
			this.scrollToElement();
			this.loginPopup();
			this.productManage();
			this.ratingTooltip();
			this.windowClick();
			this.popupMenu();
			this.topNotice();
			this.ratingForm();
			this.parallax();
			this.sideMenu();
			this.miniPopup.init();
			this.initProductSinglePage();
			this.initCollapsibleWidget();
			this.initProductsScrollLoad( '.scroll-load' );
			this.productsCartAction();
			this.productsWishlistAction();
			this.initPurchasedMinipopup();
			this.initJqueryParallax();
			this.ajaxLoadProducts();
			this.categoryNavScroll();
			this.wordRotate();
			this.footerReveal();
			this.videoModal();
			this.animPlayBtn();

			/* Menu via superfish plugin */
			if ( $.fn.superfish ) {
				this.menuInit();
			}

			/* CoundDown */
			if ( $.fn.countdown ) {
				this.countDown();
			}

			/* Call function if Owl Carousel plugin is included */
			if ( $.fn.owlCarousel ) {
				this.owlCarousels();
			}

			/* Call function if noUiSlider plugin is included - for category pages */
			if ( typeof noUiSlider === 'object' ) {
				this.filterSlider();
			}

			/* Call if not mobile and plugin is included */
			if ( $.fn.themeSticky ) {
				this.stickySidebar();
			}

			/* Call function if Light Gallery plugin is included */
			if ( $.fn.magnificPopup ) {
				this.lightBox();
			}

			/* Images grid if isotope plugin is included */
			if ( $.fn.isotope ) {
				this.isotopes();
			}
			/* Zoom image if elevateZoom plugin is included */
			if ( $.fn.elevateZoom ) {
				this.zoomImage();
			}

			if ( $.fn.themePluginFloatElement ) {
				this.floatElement();
			}
		},

		// Float Element
		floatElement: function () {
			$( function () {
				$( 'body' ).find( '[data-plugin-float-element]:not(.manual)' ).each( function () {
					var $this = $( this ),
						opts;

					var pluginOptions = $this.data( 'plugin-options' );
					if ( pluginOptions )
						opts = pluginOptions;
					if ( typeof opts == 'string' ) {
						try {
							opts = JSON.parse( opts.replace( /'/g, '"' ).replace( ';', '' ) );
						} catch ( e ) { }
					}

					$this.themePluginFloatElement( opts );
				} );
			} );
		},

		animPlayBtn: function () {
			$( document ).on( 'click', '.anim-play-btn', function () {
				var $target = $( this ).closest( ".anim-pane" ).find( '[data-animation-name]' ),
					name;
				name = $target.attr( 'data-animation-name' );
				$target.removeClass( name );
				setTimeout( function () {
					$target.addClass( name );
				}, 200 );
			} );
		},

		checkMobile: function () {
			/* Mobile Detect*/
			if ( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test( navigator.userAgent ) ) {
				this.mobile = true;
			} else {
				this.mobile = false;
			}
		},
		initCollapsibleWidget: function () {
			// slideToggle
			$( '.sidebar-wrapper .widget-title' ).on( 'click', function ( e ) {
				setTimeout( function () {
					Porto.stickySidebar();
				}, 320 );
			} );
		},
		countDown: function () {
			$( '.product-countdown' ).add( '.countdown' ).each( function () {
				var $this = $( this ),
					untilDate = $this.data( 'until' ),
					compact = $this.data( 'compact' ),
					dateFormat = ( !$this.data( 'format' ) ) ? 'DHMS' : $this.data( 'format' ),
					newLabels = ( !$this.data( 'labels-short' ) ) ?
						[ 'Years', 'Months', 'Weeks', 'Days', 'Hours', 'Minutes', 'Seconds' ] :
						[ 'Years', 'Months', 'Weeks', 'Days', 'Hours', 'Mins', 'Secs' ],
					newLabels1 = ( !$this.data( 'labels-short' ) ) ?
						[ 'Year', 'Month', 'Week', 'Day', 'Hour', 'Minute', 'Second' ] :
						[ 'Year', 'Month', 'Week', 'Day', 'Hour', 'Min', 'Sec' ];

				var newDate;

				// Split and created again for ie and edge 
				if ( !$this.data( 'relative' ) ) {
					var untilDateArr = untilDate.split( ", " ), // data-until 2019, 10, 8 - yy,mm,dd
						newDate = new Date( untilDateArr[ 0 ], untilDateArr[ 1 ] - 1, untilDateArr[ 2 ] );
				} else {
					newDate = untilDate;
				}

				$this.countdown( {
					until: newDate,
					format: dateFormat,
					padZeroes: true,
					compact: compact,
					compactLabels: [ 'y', 'm', 'w', ' days,' ],
					timeSeparator: ' : ',
					labels: newLabels,
					labels1: newLabels1

				} );
			} );
		},
		appearAnimate: function () {
			var times = new Array();
			$( '.owl-item [data-animation-name="splitRight"]' ).each( function () {
				var text = $( this ).text();
				var delay = ( $( this ).data( "animation-delay" ) ? $( this ).data( "animation-delay" ) : "0" );
				$( this ).text( "" );
				for ( var j = text.length - 1; j >= 0; j-- ) {
					$( this ).prepend( '<div class="d-inline-block appear-animate" data-animation-delay="' + ( delay + 90 * j ) + '">' + ( text[ j ] === " " ? '&nbsp' : text[ j ] ) + '</div>' );
				}
			} );
			this.intObs( '.appear-animate', function () {
				if ( !$( this ).hasClass( 'animated' ) ) {
					var $this = $( this );
					if ( $this.closest( ".owl-carousel.slide-animate" ).length > 0 )
						if ( $this.closest( '.owl-item.active' ).length === 0 )
							return;
					var name, delay, duration;
					name = ( $this.data( "animation-name" ) ? $this.data( "animation-name" ) : "fadeIn" );
					duration = ( $this.data( "animation-duration" ) ? $this.data( "animation-duration" ) : "1000" );
					delay = ( $this.data( "animation-delay" ) ? $this.data( "animation-delay" ) : "0" );
					$this.addClass( "animated" );
					var id = setTimeout( function () {
						$this.addClass( name );
						$this.css( 'animationDuration', duration + "ms" );
						$this.addClass( "appear-animation-visible" );
					}, parseInt( delay ? delay : 0 ) );
					if ( $this.closest( ".owl-carousel.slide-animate" ).length > 0 )
						times.push( id );
				}
			}, {} );
			$( ".appear-animate-svg" ).each( function () {
				if ( !$( this ).hasClass( 'animated' ) ) {
					$( this ).appear( function () {
						var $this = $( this ), name, delay, duration;
						name = ( $this.data( "animation-name" ) ? $this.data( "animation-name" ) : "customLineAnim" );
						duration = ( $this.data( "animation-duration" ) ? $this.data( "animation-duration" ) : "1000" );
						delay = ( $this.data( "animation-delay" ) ? $this.data( "animation-delay" ) : "0" );
						$this.addClass( "animated" );
						var id = setTimeout( function () {
							$this.addClass( name );
							$this.css( 'animationDuration', duration + "ms" );
							$this.addClass( "appear-animation-visible" );
						}, parseInt( delay ? delay : 0 ) );
						times.push( id );
					}, {
						accX: $( this ).data( 'x' ) ? $( this ).data( 'x' ) : 0,
						accY: $( this ).data( 'y' ) ? $( this ).data( 'y' ) : -50
					} );
				}
			} );
			$( ".owl-carousel.slide-animate" ).each( function () {
				var translateCarousel;
				$( this ).on( 'translate.owl.carousel', function ( event ) {
					translateCarousel = $( this ).find( ".owl-item.active" );
				} );

				$( this ).on( 'translated.owl.carousel', function ( event ) {
					var item = $( this );

					if ( $( this ).find( ".owl-item.active" )[ 0 ] !== translateCarousel[ 0 ] ) {
						for ( var i = 0; i < times.length; i++ )
							clearTimeout( times[ i ] );
						times = times.splice();
						translateCarousel.find( ".appear-animate" ).removeClass( "appear-animation-visible" );
						translateCarousel.find( ".appear-animate" ).css( 'animationDelay', "" );
						translateCarousel.find( ".appear-animate" ).css( 'animationDuration', "" );
						translateCarousel.find( ".appear-animate" ).removeClass( "animated" );
						translateCarousel.find( ".appear-animate" ).each( function () {
							var $this = $( this );
							var name;
							name = ( $this.data( "animation-name" ) ? $this.data( "animation-name" ) : "fadeIn" );
							$this.removeClass( name );
						} );
					}
					item.find( ".owl-item.active .appear-animate" ).each( function () {
						var $this = $( this );
						var name, delay, duration;
						name = ( $this.data( "animation-name" ) ? $this.data( "animation-name" ) : "fadeIn" );
						duration = ( $this.data( "animation-duration" ) ? $this.data( "animation-duration" ) : "1000" );
						delay = ( $this.data( "animation-delay" ) ? $this.data( "animation-delay" ) : "0" );

						$this.addClass( name );
						if ( name != "splitRight" ) {
							$this.css( 'animationDelay', delay + "ms" );
							$this.css( 'animationDuration', duration + "ms" );
						}
						$this.addClass( "animated" );
						var id = setTimeout( function () {
							$this.addClass( "appear-animation-visible" );
						}, parseInt( delay ? delay : 0 ) );
						times.push( id );
					} );
				} );
			} );
		},
		initProductsScrollLoad: function ( $obj ) {
			var $wrapper = $( $obj )
				, top;

			var loadProducts = function ( e ) {
				if ( window.pageYOffset > top + $wrapper.outerHeight() - window.innerHeight - 150 && 'loading' != $wrapper.data( 'load-state' ) ) {
					$.ajax( {
						url: 'ajax/category-ajax-products.html',
						success: function ( result ) {
							var $newItems = $( result );
							$wrapper.data( 'load-state', 'loading' );

							if ( !$wrapper.next().hasClass( 'bounce-loader' ) ) {
								$( '<div class="bounce-loader"><div class="bounce1"></div><div class="bounce2"></div><div class="bounce3"></div></div>' ).insertAfter( $wrapper );
							}

							setTimeout( function () {
								$wrapper.append( $newItems );

								setTimeout( function () {
									$wrapper.find( '.col-6.fade:not(.in)' ).addClass( 'in' );
								}, 200 );
								$wrapper.data( 'load-state', 'loaded' );
								loadCount >= 2 && $wrapper.next().css( 'display', 'none' );
							}, 1500 );

							var loadCount = parseInt( $wrapper.data( 'load-count' ) ? $wrapper.data( 'load-count' ) : 0 );
							$wrapper.data( 'load-count', ++loadCount );
							loadCount >= 2 && window.removeEventListener( 'scroll', loadProducts, { passive: true } );
						},
						failure: function () {
							$this.text( "Sorry something went wrong." );
						}
					} );
				}
			}

			if ( $wrapper.length > 0 ) {
				top = $wrapper.offset().top;
				window.addEventListener( 'scroll', loadProducts, { passive: true } );
			}
		},
		menuInit: function () {
			// General Menu
			$( '.menu:not(.menu-vertical):not(.no-superfish)' ).superfish( {
				popUpSelector: 'ul, .megamenu',
				hoverClass: 'show',
				delay: 0,
				speed: 80,
				speedOut: 80,
				autoArrows: true
			} );

			// Vertical Menu
			$( '.menu.menu-vertical.no-animation' ).superfish( {
				popUpSelector: 'ul, .megamenu',
				hoverClass: 'show',
				delay: 0,
				speed: 200,
				speedOut: 200,
				autoArrows: true,
			} );

			$( '.menu.menu-vertical:not(.no-superfish)' ).superfish( {
				popUpSelector: 'ul, .megamenu',
				hoverClass: 'show',
				delay: 0,
				speed: 200,
				speedOut: 200,
				autoArrows: true,
				animation: {
					left: "100%",
					opacity: "show"
				},
				animationOut: {
					left: "90%",
					opacity: "hide"
				}
			} );

			// Calculate fixed-width megamenu's position.
			var calculateMegaMenuPosition = function () {
				$( '.menu:not(.menu-vertical):not(.no-superfish) .megamenu-fixed-width' ).each( function () {
					var $menu = $( this ),
						$left = $menu.parent().offset().left - 15,
						$width = $menu.outerWidth(),
						$right_max = $( window ).width() - 45,
						$right_space = $right_max - $left - $width;

					if ( $right_space < 0 ) {
						$menu.css( 'left', $right_space + 'px' );
					} else {
						$menu.css( 'left', '-15px' );
					}
				} );
			}
			calculateMegaMenuPosition();

			// Recalculate menu position on resize event.
			$.fn.smartresize ?
				$( window ).smartresize( calculateMegaMenuPosition ) :
				$( window ).on( 'resize', calculateMegaMenuPosition );
		},
		stickyHeader: function () {
			var isInitialised = false;
			var objectsArray = null;
			var optionsArray = null;
			var isMobile = 992 > $( window ).width();

			// Fix Sticky Header
			var fixStickyHeader = function ( $stickyHeader, options, stickyOffset ) {
				// If already fixed, return.
				if ( $stickyHeader.hasClass( 'fixed' ) ) {
					return;
				}

				// Set wrapper's min-height - sticky header's placeholder.
				$stickyHeader.parent().css( 'min-height', options.height );

				objectsArray.filter( '.fixed' ).each( function () {
					stickyOffset += $( this ).outerHeight();
				} );

				// Fix sticky header
				$stickyHeader
					.addClass( 'fixed' )
					.css( 'top', - options.height )
					.animate( { top: stickyOffset } );

				$stickyHeader.find( '.product-action' ).removeClass( 'd-none' );
			};

			// Unfix Sticky Header
			var unfixStickyHeader = function ( $stickyHeader, options ) {
				// If already unfixed, return.
				if ( !$stickyHeader.hasClass( 'fixed' ) ) {
					return;
				}

				// Unfix sticky header
				$stickyHeader.removeClass( 'fixed' );
				$stickyHeader.css( 'top', '' );


				// Unset wrapper's min-height - sticky header's placeholder.
				$stickyHeader.parent().css( 'min-height', '' );
				if ( !$stickyHeader.find( '.product-action' ).hasClass( 'd-none' ) > 0 ) {
					$stickyHeader.find( '.product-action' ).addClass( 'd-none' );
				}
			};

			// Setup sticky header on only desktop.
			var setupStickyHeader = function () {
				if ( !isInitialised ) {
					optionsArray = [];

					// Init Sticky Header
					objectsArray = $( '.sticky-header' ).each( function () {
						var $this = $( this ),
							userOptions = $this.data( 'sticky-options' ),
							options = {};

						if ( userOptions ) {
							options = JSON.parse( userOptions.replace( /'/g, '"' ).replace( ';', '' ) );
						}

						// Get original properties.
						options.height = $this.outerHeight( true );
						options.offset = options.offset || $this.offset().top;
						options.paddingTop = parseInt( $this.css( 'padding-top' ) );

						optionsArray.push( options );

						// Wrap sticky header.
						$this.parent().hasClass( 'sticky-wrapper' ) || $this.wrap( '<div class="sticky-wrapper"></div>' );
					} );

					isInitialised = true;
				} else {
					// Mobile
					optionsArray.forEach( function ( option, index ) {
						if ( option.mobile === !isMobile ) {
							var $object = $( objectsArray[ index ] );
							var userOptions = JSON.parse( $object.data( 'sticky-options' ).replace( /'/g, '"' ).replace( ';', '' ) );
							option.height = $object.outerHeight( true );
							option.offset = userOptions.offset ? userOptions.offset : $object.offset().top;
							option.paddingTop = parseInt( $object.css( 'padding-top' ) );
						} else if ( option.mobile === isMobile ) {
							var $object = $( objectsArray[ index ] );
							unfixStickyHeader( $object, option );
						}
					} );
				}
			};

			// Build and rebuild sticky header.
			var buildStickyHeader = function () {
				// if mobile & tablet
				if ( 992 > $( window ).width() ) {

					// if sticky header is initialised, fix it.
					( isMobile && isInitialised ) || setupStickyHeader();

					isMobile = true;

					var scrollTop = $( window ).scrollTop();
					var stickyOffset = 0;

					objectsArray.each( function ( index ) {
						var $this = $( this ),
							options = optionsArray[ index ];

						// On Sticky, fix
						if ( options.mobile !== false && scrollTop + stickyOffset > options.offset + options.paddingTop ) {
							$this.hasClass( 'fixed' ) || fixStickyHeader( $this, options, stickyOffset );

							// On Unsticky, unfix
						} else if ( options.mobile === true || $this.hasClass( 'fixed' ) || !isInitialised ) {
							unfixStickyHeader( $this, options );
						}
					} );

					// Sticky Navbar
					if ( $( '.sticky-navbar' ) && 576 > $( window ).width() ) {
						var scrollTop = $( window ).scrollTop();

						if ( scrollTop >= 300 ) {
							$( '.sticky-navbar' ).addClass( 'fixed' );

							// On Unsticky, unfix
						} else {
							$( '.sticky-navbar' ).removeClass( 'fixed' );
						}
					}

					// if desktop
				} else {
					// Initialize sticky header
					( !isMobile && isInitialised ) || setupStickyHeader();

					isMobile = false;

					// Calculate sticky header's top position
					var scrollTop = $( window ).scrollTop();
					var stickyOffset = 0;

					objectsArray.each( function ( index ) {
						var $this = $( this ),
							options = optionsArray[ index ];

						// On Sticky, fix
						if ( options.mobile !== true && scrollTop + stickyOffset > options.offset + options.paddingTop ) {
							$this.hasClass( 'fixed' ) || fixStickyHeader( $this, options, stickyOffset );
							// stickyOffset += $this.outerHeight();

							// On Unsticky, unfix
						} else if ( options.mobile === false || $this.hasClass( 'fixed' ) || !isInitialised ) {
							unfixStickyHeader( $this, options );
						}
					} );
				}
			}

			// init
			setTimeout( buildStickyHeader, 500 );
			$( window ).smartresize( buildStickyHeader );
			$( window ).on( 'scroll', buildStickyHeader );
		},
		headerSearchToggle: function () {
			$( '.header-search' ).length &&
				$( 'body' )
					// Stop Propagation
					.on( 'click', '.header-search', function ( e ) {
						e.stopPropagation();
					} )

					// Search Toggle
					.on( 'click', '.search-toggle', function ( e ) {
						var $headerSearch = $( this ).closest( '.header-search' );

						$headerSearch.toggleClass( 'show' );
						$( '.header-search-wrapper' ).toggleClass( 'show' ); // Will be deprecated.

						$headerSearch.hasClass( 'show' ) && $headerSearch.find( 'input.form-control' ).focus();
						e.preventDefault();
					} )

					// Search Deactive
					.on( 'click', function ( e ) {
						$( '.header-search' ).removeClass( 'show' );
						$( '.header-search-wrapper' ).removeClass( 'show' ); // Will be deprecated.
						$( 'body' ).removeClass( 'is-search-active' );
					} );

			var calcHeaderSearchPosition = function () {
				$( '.header-search' ).each( function () {
					var $this = $( this );
					$this.find( '.header-search-wrapper' ).css(
						$( window ).width() < 576 ?
							{
								left: 15 - $this.offset().left + 'px',
								right: 25 + $this.offset().left + $this.width() - $( window ).width() + 'px'
							} :
							{
								left: '',
								right: ''
							}
					);
				} );
			};

			calcHeaderSearchPosition();

			$.fn.smartresize ?
				$( window ).smartresize( calcHeaderSearchPosition ) :
				$( window ).on( 'resize', calcHeaderSearchPosition );
		},
		mMenuToggle: function () {
			if ( $( '.sidebar-home' ).find( '.menu-vertical' ).length > 0 ) {
				$( '.menu-vertical .menu-btn' ).on( 'click', function ( e ) {
					if ( window.innerWidth < 992 ) {
						var $this = $( this );

						e.preventDefault();
						e.stopPropagation();
						$this.closest( 'li' ).find( '>ul, .megamenu' ).slideToggle();
					}
				} );
			}

			$( '.mobile-menu-toggler' ).on( 'click', function ( e ) {
				$( 'body' ).toggleClass( 'mmenu-active' );
				$( this ).toggleClass( 'active' );
				e.preventDefault();
			} );

			// Menu Show/Hide // Used in Demo 12
			$( '.menu-toggler' ).on( 'click', function ( e ) {
				if ( $( window ).width() >= 992 ) {
					$( '.main-dropdown-menu' ).toggleClass( 'show' );
				} else {
					$( 'body' ).toggleClass( 'mmenu-active' );
				}
				e.preventDefault();
			} );

			// Hide Menu
			$( '.mobile-menu-overlay, .mobile-menu-close' ).on( 'click', function ( e ) {
				$( 'body' ).removeClass( 'mmenu-active' );
				$( '.menu-toggler' ).removeClass( 'active' );
				e.preventDefault();
			} );

			$( '.menu-item > a' ).on( 'click', function ( e ) {
				$( 'body' ).toggleClass( 'mmenu-depart-active' );
				$( this ).siblings( '.menu-depart' ).toggleClass( 'opened' );
				e.preventDefault();
			} );
		},
		mMenuIcons: function () {
			// Add Mobile menu icon arrows or plus/minus to items with children
			$( '.mobile-menu' ).find( 'li' ).each( function () {
				var $this = $( this );

				if ( $this.find( 'ul' ).length ) {
					$( '<span/>', {
						'class': 'mmenu-btn'
					} ).appendTo( $this.children( 'a' ) );
				}
			} );
		},
		mobileMenu: function () {
			// Mobile Menu Toggle
			$( '.mmenu-btn' ).on( 'click', function ( e ) {
				var $parent = $( this ).closest( 'li' ),
					$targetUl = $parent.find( 'ul' ).eq( 0 );

				if ( !$parent.hasClass( 'open' ) ) {
					$targetUl.slideDown( 300, function () {
						$parent.addClass( 'open' );
					} );
				} else {
					$targetUl.slideUp( 300, function () {
						$parent.removeClass( 'open' );
					} );
				}

				e.stopPropagation();
				e.preventDefault();
			} );
		},
		owlCarousels: function () {
			var sliderDefaultOptions = {
				loop: true,
				margin: 0,
				responsiveClass: true,
				nav: false,
				navText: [ '<i class="icon-angle-left">', '<i class="icon-angle-right">' ],
				dots: true,
				autoplay: false,
				autoplayTimeout: 15000,
				items: 1,
			};

			var sliderInit = function ( $slider, sliderCustomOptions ) {

				var newSliderOptions;

				if ( sliderCustomOptions ) {
					newSliderOptions = $.extend( true, {}, sliderDefaultOptions, sliderCustomOptions );
				} else {
					newSliderOptions = sliderDefaultOptions;
				}

				$slider.hasClass( 'nav-thin' ) &&
					( newSliderOptions.navText = [ '<i class="icon-left-open-big">', '<i class="icon-right-open-big">' ] );

				var userOptions = $slider.data( 'owl-options' );
				if ( typeof userOptions == 'string' ) {
					userOptions = JSON.parse( userOptions.replace( /'/g, '"' ).replace( ';', '' ) );
					newSliderOptions = $.extend( true, {}, newSliderOptions, userOptions );
				}
				$slider.on( 'initialize.owl.carousel', onInitialize );
				$slider.owlCarousel( newSliderOptions );
			}

			var onInitialize = function ( e ) {
				var i, j, breaks = [ '', '-xs', '-sm', '-md', '-lg', '-xl', '-xxl' ];
				this.classList.remove( 'row' );
				for ( i = 0; i < 7; ++i ) {
					for ( j = 1; j <= 12; ++j ) {
						this.classList.remove( 'cols' + breaks[ i ] + '-' + j );
					}
				}
				this.classList.remove( 'gutter-no' );
				this.classList.remove( 'gutter-sm' );
				this.classList.remove( 'gutter-lg' );
			}

			var sliderCustomOptionsArray = {
				'.home-slider': {
					lazyLoad: false,
					autoplay: false,
					dots: false,
					nav: true,
					autoplayTimeout: 12000,
					animateOut: 'fadeOut',
					navText: [ '<i class="icon-left-open-big">', '<i class="icon-right-open-big">' ],
					loop: true
				},
				'.testimonials-carousel': {
					lazyLoad: false,
					autoHeight: true,
					responsive: {
						992: {
							items: 2
						}
					}
				},
				'.featured-products': {
					loop: false,
					margin: 30,
					autoplay: false,
					responsive: {
						0: {
							items: 2
						},
						700: {
							items: 3,
							margin: 15
						},
						1200: {
							items: 4
						}
					}
				},
				'.cats-slider': {
					loop: false,
					margin: 20,
					autoplay: false,
					nav: true,
					dots: false,
					items: 2,
					responsive: {
						576: {
							items: 3
						},
						992: {
							items: 4,
						},
						1200: {
							items: 5,
						},
						1400: {
							items: 6
						}
					}
				},

				'.products-slider.5col': {
					loop: false,
					margin: 20,
					autoplay: false,
					dots: false,
					items: 2,
					responsive: {
						576: {
							items: 3
						},
						768: {
							items: 4
						},
						992: {
							items: 5,
						}
					}
				},

				'.products-slider': {
					loop: false,
					margin: 20,
					autoplay: false,
					dots: true,
					items: 2,
					responsive: {
						576: {
							items: 3
						},
						992: {
							items: 4,
						}
					}
				},

				'.categories-slider': {
					loop: false,
					margin: 20,
					autoplay: false,
					nav: true,
					dots: false,
					items: 2,
					responsive: {
						576: {
							items: 3
						},
						992: {
							items: 5,
						}
					}
				},

				'.quantity-inputs': {
					items: 2,
					margin: 20,
					dots: false,
					nav: true,
					responsive: {
						992: {
							items: 4
						},
						768: {
							items: 3
						}
					},
					onInitialized: function () {
						this.$element.find( '.horizontal-quantity' ).val( 1 );
					}
				},

				'.banners-slider': {
					dots: true,
					loop: false,
					margin: 20,
					responsive: {
						576: {
							items: 2
						},
						992: {
							items: 3
						}
					}
				},

				'.brands-slider': {
					loop: false,
					margin: 20,
					autoHeight: true,
					autoplay: false,
					dots: false,
					navText: [ '<i class="icon-left-open-big">', '<i class="icon-right-open-big">' ],
					items: 2,
					responsive: {
						0: {
							items: 2
						},
						480: {
							items: 3
						},
						768: {
							items: 4
						},
						991: {
							items: 5
						},
						1200: {
							items: 6
						}
					}
				},

				'.instagram-feed-carousel': {
					loop: true,
					dots: false,
					autoplay: false,
					responsive: {
						0: {
							items: 2
						},
						480: {
							items: 3
						},
						768: {
							items: 5
						},
						992: {
							items: 6
						},
						1200: {
							items: 7
						},
						1400: {
							items: 8
						},
						1600: {
							items: 9
						},
						1800: {
							items: 10
						}
					}
				},

				'.widget-featured-products': {
					margin: 20,
					lazyLoad: true,
					nav: true,
					navText: [ '<i class="icon-angle-left">', '<i class="icon-angle-right">' ],
					dots: false,
					autoHeight: true
				},

				'.entry-slider': {
					margin: 0,
					lazyLoad: true,
				},

				// Single.html - Related posts
				'.related-posts-carousel': {
					loop: false,
					margin: 30,
					autoplay: false,
					responsive: {
						480: {
							items: 2
						},
						1200: {
							items: 3
						}
					}
				},

				'.boxed-slider': {
					lazyLoad: true,
					autoplayTimeout: 20000,
					animateOut: 'fadeOut',
					dots: false
				},

				// About Slider
				'.about-slider': {
					margin: 0,
					lazyLoad: true
				},

				// Product single carousel - Default layout
				'.product-single-default .product-single-carousel': {
					margin: 0,
					nav: true,
					loop: false,
					dotsContainer: '#carousel-custom-dots',
					autoplay: false,
					onResized: function () {
						var $source = this.$element;

						if ( $.fn.elevateZoom ) {
							$source.find( 'img' ).each( function () {
								var $this = $( this ),
									zoomConfig = {
										responsive: true,
										zoomWindowFadeIn: 350,
										zoomWindowFadeOut: 200,
										borderSize: 0,
										zoomContainer: $this.parent(),
										zoomType: 'inner',
										cursor: 'grab'
									};

								$this.elevateZoom( zoomConfig );
							} );
						}
					},

					onInitialized: function () {
						var $source = this.$element;

						if ( $.fn.elevateZoom ) {
							$source.find( 'img' ).each( function () {
								var $this = $( this ),
									zoomConfig = {
										responsive: true,
										zoomWindowFadeIn: 350,
										zoomWindowFadeOut: 200,
										borderSize: 0,
										zoomContainer: $this.parent(),
										zoomType: 'inner',
										cursor: 'grab'
									};

								$this.elevateZoom( zoomConfig );
							} );
						}
					}
				},

				// Product single carousel - Extended layout
				'.product-single-extended .product-single-carousel': {
					dots: false,
					autoplay: false,
					center: true,
					items: 1,
					responsive: {
						768: {
							items: 3
						}
					}
				}
			};

			// Init custom carousels
			var selectors = Object.keys( sliderCustomOptionsArray );
			selectors.forEach( function ( selector ) {
				$( selector ).each( function () {
					sliderInit( $( this ), sliderCustomOptionsArray[ selector ] );
				} );
			} );

			// Init all carousels except custom carousels.
			$( '.owl-carousel' ).each( function () {
				if ( !$( this ).data( 'owl.carousel' ) )
					sliderInit( $( this ), sliderInit );
			} );

			// Add loaded class on lazy load
			$( '.home-slider' ).on( 'loaded.owl.lazy', function ( event ) {
				$( event.element ).closest( '.home-slide' ).addClass( 'loaded' );
				$( event.element ).closest( '.home-slider' ).addClass( 'loaded' ); // For Demo 12
			} );
			$( '.boxed-slider' ).on( 'loaded.owl.lazy', function ( event ) {
				$( event.element ).closest( '.category-slide' ).addClass( 'loaded' );
			} );
			$( '.about-slider' ).on( 'loaded.owl.lazy', function ( event ) {
				$( event.element ).closest( 'div' ).addClass( 'loaded' );
			} );

			// Home Slider with custom dots container
			$( '.home-slider.with-dots-container' ).each( function () {
				var $slider = $( this );
				var $thumbs = $slider.parent().find( '.home-slider-thumbs' );
				var $dots = $thumbs.children();
				var $options = {
					nav: false,
					dots: false,
					items: 3,
					margin: 10
				};
				$dots.eq( 0 ).addClass( 'active' );

				$thumbs.addClass( 'owl-carousel owl-theme' ).owlCarousel( $options );
				$dots.click( function () {
					var $this = $( this ),
						index = ( $this.parent().filter( $thumbs ).length ? $this : $this.parent() ).index();
					$slider.trigger( 'to.owl.carousel', index );
				} );
				$slider.on( 'translate.owl.carousel', function ( e ) {
					var currentIndex = ( e.item.index - $( e.currentTarget ).find( '.cloned' ).length / 2 + e.item.count ) % e.item.count;
					var $curThumb = $dots.eq( currentIndex );
					$dots.filter( '.active' ).removeClass( 'active' );
					$curThumb.addClass( 'active' );
					$thumbs.trigger( 'to.owl.carousel', currentIndex );
				} );
			} );

			// Home Slider with sidebar
			$( '.home-slider-sidebar' ).each( function () {
				var $thumbs = $( this );
				var $slider = $thumbs.parent().find( '.home-slider' ),
					$dots = $thumbs.find( 'li' );

				$dots.click( function () {
					var $this = $( this ),
						index = $this.index();
					$slider.trigger( 'to.owl.carousel', index );
				} );
				$slider.on( 'translate.owl.carousel', function ( e ) {
					var currentIndex = ( e.item.index - $( e.currentTarget ).find( '.cloned' ).length / 2 + e.item.count ) % e.item.count;
					var $curThumb = $dots.eq( currentIndex );
					$dots.filter( '.active' ).removeClass( 'active' );
					$curThumb.addClass( 'active' );
					$thumbs.trigger( 'to.owl.carousel', currentIndex );
				} );
			} );
		},
		filterSlider: function () {
			// Slider For category pages / filter price
			var priceSlider = document.getElementById( 'price-slider' ),
				currencyVar = '$';

			// Check if #price-slider elem is exists if not return
			// to prevent error logs
			if ( priceSlider == null ) return;

			noUiSlider.create( priceSlider, {
				start: [ 0, 1000 ],
				connect: true,
				step: 100,
				margin: 100,
				range: {
					'min': 0,
					'max': 1000
				}
			} );

			// Update Price Range
			priceSlider.noUiSlider.on( 'update', function ( values, handle ) {
				var values = values.map( function ( value ) {
					return currencyVar + parseInt( value );
				} )

				$( '#filter-price-range' ).text( values.join( ' - ' ) );
			} );
		},
		stickySidebar: function () {
			var paddingOffsetTop = 10;
			var sidebarOption = 992;
			$( '.header-bottom.sticky-header' ).each( function () {
				paddingOffsetTop += $( this ).height();
			} );

			$( '.sidebar-wrapper' ).each( function () {
				if ( $( this ).data( "sticky-sidebar-options" ) ) {
					paddingOffsetTop = $( this ).data( "sticky-sidebar-options" ).offsetTop;
					sidebarOption = $( this ).data( "sticky-sidebar-options" ).minWidth;
				}
			} );

			$( ".sidebar-wrapper, .sticky-slider" ).themeSticky( {
				autoInit: true,
				minWidth: sidebarOption,
				containerSelector: '.row, .container',
				paddingOffsetBottom: 10,
				paddingOffsetTop: paddingOffsetTop
			} );
		},
		countTo: function () {
			// CountTo plugin used count animations for homepages
			if ( $.fn.numerator ) {
				$( '.count-to' ).each( function () {
					var $this = $( this ),
						options = {
							fromValue: $this.data( 'fromvalue' ),
							toValue: $this.data( 'tovalue' ),
							duration: $this.data( 'duration' ),
							delimiter: $this.data( 'delimiter' ),
							rounding: $this.data( 'round' )
						};

					$.extend( options, {
						onComplete: function () {
							if ( $this.data( 'append' ) ) {
								$this.html( $this.html() + $this.data( 'append' ) );
							}

							if ( $this.data( 'prepend' ) ) {
								$this.html( $this.data( 'prepend' ) + $this.html() );
							}
						}
					} );
					$this.appear( function () {
						setTimeout( function () {
							$this.numerator( options );
						}, 300 );
					} )
				} );
			}
		},
		alert: function () {
			$( '.alert-dismissible' ).each( function () {
				$( this ).append( '<button class="alert-close" />' );
			} );
			$( '.alert-dismissible .alert-close' ).on( 'click', function ( e ) {
				var $parent = $( this ).closest( '.alert-dismissible' );
				$parent.fadeOut( function () {
					$parent.remove();
				} );
			} );
		},
		tooltip: function () {
			// Bootstrap Tooltip
			if ( $.fn.tooltip ) {
				$( '[data-toggle="tooltip"]' ).tooltip( {
					trigger: 'hover focus' // click can be added too
				} );
			}
		},
		popover: function () {
			// Bootstrap Popover
			if ( $.fn.popover ) {
				$( '[data-toggle="popover"]' ).popover( {
					trigger: 'focus'
				} );
			}
		},
		changePassToggle: function () {
			// Toggle new/change password section via checkbox
			$( '#change-pass-checkbox' ).on( 'change', function () {
				$( '#account-chage-pass' ).toggleClass( 'show' );
			} );
		},
		changeBillToggle: function () {
			// Checkbox review - billing address checkbox
			$( '#change-bill-address' ).on( 'change', function () {
				$( '#checkout-shipping-address' ).toggleClass( 'show' );
				$( '#new-checkout-address' ).toggleClass( 'show' );
			} );
		},
		catAccordion: function () {
			// Toggle "open" Class for parent elem - Home cat widget
			$( '.catAccordion' ).on( 'shown.bs.collapse', function ( item ) {
				var parent = $( item.target ).closest( 'li' );

				if ( !parent.hasClass( 'open' ) ) {
					parent.addClass( 'open' );
				}
			} ).on( 'hidden.bs.collapse', function ( item ) {
				var parent = $( item.target ).closest( 'li' );

				if ( parent.hasClass( 'open' ) ) {
					parent.removeClass( 'open' );
				}
			} );
		},
		scrollBtnAppear: function () {
			if ( $( window ).scrollTop() >= 400 ) {
				$( '#scroll-top' ).addClass( 'fixed' );
			} else {
				$( '#scroll-top' ).removeClass( 'fixed' );
			}
		},
		scrollToTop: function () {
			$( '#scroll-top' ).on( 'click', function ( e ) {
				$( 'html, body' ).animate( {
					'scrollTop': 0
				}, 1200 );
				e.preventDefault();
			} );
		},
		newsletterPopup: function () {
			$.magnificPopup.open( {
				items: {
					src: '#newsletter-popup-form'
				},
				type: 'inline',
				mainClass: 'mfp-newsletter',
				removalDelay: 350,
				callbacks: {
					open: function () {
						if ( $( '.sticky-header.fixed' ).css( 'padding-right' ) ) {
							$( '.sticky-header.fixed' ).css( 'padding-right', window.innerWidth - document.body.clientWidth );
							$( '.sticky-header.fixed-nav' ).css( 'padding-right', window.innerWidth - document.body.clientWidth );
							$( '#scroll-top' ).css( 'margin-right', window.innerWidth - document.body.clientWidth );
							$( '.minipopup-area' ).css( 'padding-right', window.innerWidth - document.body.clientWidth );
							$( '.wishlist-popup' ).css( 'padding-right', window.innerWidth - document.body.clientWidth );
						}
					},
					afterClose: function () {
						if ( $( '.sticky-header.fixed' ).css( 'padding-right' ) ) {
							$( '.sticky-header.fixed' ).css( 'padding-right', '' );
							$( '.sticky-header.fixed-nav' ).css( 'padding-right', '' );
							$( '#scroll-top' ).css( 'margin-right', '' );
							$( '.minipopup-area' ).css( 'padding-right', '' );
							$( '.wishlist-popup' ).css( 'padding-right', '' );
						}
					}
				},
			} );
		},

		lightBox: function () {
			// Newsletter popup
			if ( document.getElementById( 'newsletter-popup-form' ) ) {
				setTimeout( function () {
					var mpInstance = $.magnificPopup.instance;
					if ( mpInstance.isOpen ) {
						mpInstance.close();
						setTimeout( function () {
							Porto.newsletterPopup();
						}, 360 );
					}
					else {
						Porto.newsletterPopup();
					}
				}, 5000 );
			}

			// Gallery Lightbox
			var links = [];
			var $productSliderImages;

			if ( $( '.product-slider-container' ).length > 0 ) {
				$productSliderImages = $( '.product-single-carousel .owl-item:not(.cloned) img' );
			} else {
				$productSliderImages = $( '.product-single-gallery img' );
			}

			if ( $( '.media-with-zoom' ).length > 0 ) {
				$productSliderImages = $( '.media-with-zoom .owl-carousel .owl-item:not(.cloned) img' );
			}

			if ( $( '.team-section' ).length > 0 ) {
				$productSliderImages = $( '.team-section img' );
			}

			$productSliderImages.each( function () {
				links.push( { 'src': $( this ).attr( 'data-zoom-image' ) } );
			} );

			$( ".prod-full-screen" ).click( function ( e ) {
				var currentIndex;

				if ( e.currentTarget.closest( ".product-slider-container" ) ) {
					currentIndex = $( '.product-single-carousel' ).data( 'owl.carousel' ).current();
				} else {
					currentIndex = $( e.currentTarget ).closest( ".product-item" ).index();
				}

				if ( $( e.currentTarget ).closest( ".post" ).length > 0 ) {
					currentIndex = $( e.currentTarget ).closest( ".owl-item:not(.cloned)" ).index();
				}

				if ( $( e.currentTarget ).closest( ".team-info" ).length > 0 ) {
					currentIndex = $( e.currentTarget ).closest( ".team-info" ).parent().index();
				}

				$.magnificPopup.open( {
					items: links,
					navigateByImgClick: true,
					type: 'image',
					gallery: {
						enabled: true
					},
				}, currentIndex );
			} );

			//QuickView Popup
			$( 'body' ).on( 'click', 'a.btn-quickview', function ( e ) {
				e.preventDefault();
				var $this = $( this );

				if ( !$this.closest( '.product-default' ).find( 'figure' ).hasClass( 'load-more-overlay' ) ) {
					$this.closest( '.product-default' ).find( 'figure' ).addClass( 'load-more-overlay' );
					$this.closest( '.product-default' ).find( 'figure' ).append( '<i class="porto-loading-icon"></i>' );
				}

				var ajaxUrl = $( this ).attr( 'href' );
				setTimeout( function () {
					$.magnificPopup.open( {
						type: 'ajax',
						mainClass: "mfp-ajax-product",
						tLoading: '',
						preloader: false,
						removalDelay: 350,
						items: {
							src: ajaxUrl
						},
						callbacks: {
							open: function () {
								if ( $( '.sticky-header.fixed' ).css( 'padding-right' ) ) {
									$( '.sticky-header.fixed' ).css( 'padding-right', window.innerWidth - document.body.clientWidth );
									$( '.sticky-header.fixed-nav' ).css( 'padding-right', window.innerWidth - document.body.clientWidth );
									$( '#scroll-top' ).css( 'margin-right', window.innerWidth - document.body.clientWidth );
								}
							},

							ajaxContentAdded: function () {
								Porto.ajaxLoading();
								$this.closest( '.product-default' ).find( 'figure' ).removeClass( 'load-more-overlay' );
								$this.closest( '.product-default' ).find( 'figure .porto-loading-icon' ).remove();
								Porto.quantityInputs( $( '.mfp-ajax-product' ) );
								Porto.initProductSinglePage();
								Porto.owlCarousels();
							},

							beforeClose: function () {
								$( '.ajax-overlay' ).remove();
							},

							afterClose: function () {
								if ( $( '.sticky-header.fixed' ).css( 'padding-right' ) ) {
									$( '.sticky-header.fixed' ).css( 'padding-right', '' );
									$( '.sticky-header.fixed-nav' ).css( 'padding-right', '' );
									$( '#scroll-top' ).css( 'margin-right', '' );
								}
							}
						},
						ajax: {
							tError: '',
						}
					} );
				}, 300 );
			} );
		},
		videoModal: function () {
			// Popup - Iframe Video - Map etc.
			if ( $.fn.magnificPopup ) {
				$( '.btn-iframe' ).magnificPopup( {
					type: 'iframe',
					preloader: false,
					fixedContentPos: false,
					closeBtnInside: false
				} );
			}
		},
		linkToTab: function () {
			$( 'body' )
				.on( 'click', '.nav .nav-link', function ( e ) {

					if ( $( '.nav .nav-link' ).hasClass( 'address' ) ) {
						$( '.nav .nav-link' ).removeClass( 'address' );
					}
				} )
				.on( 'click', '.link-to-tab', function ( e ) {
					var $this = $( this ),
						selector = $this.attr( 'href' ),
						$tab = $( selector ),
						$nav = $tab.parent().parent().find( '.nav' );
					e.preventDefault();

					$tab.siblings().removeClass( 'active show' );
					$tab.addClass( 'active show' );
					$nav.find( '.nav-link' ).removeClass( 'active' );

					if ( selector == '#shipping' || selector == '#billing' ) {
						if ( !$nav.find( '[href="#address"]' ).hasClass( 'address' ) )
							$nav.find( '[href="#address"]' ).addClass( 'address' );
					} else {
						$nav.find( '[href="' + selector + '"]' ).addClass( 'active' );
					}

					$( 'html' ).animate( {
						scrollTop: $tab.offset().top - 150
					} );
				} );
		},
		productTabSroll: function () {
			// Scroll to product details tab and show review tab - product pages
			$( '.rating-link' ).on( 'click', function ( e ) {
				if ( $( '.product-single-tabs' ).length ) {
					$( '#product-tab-reviews' ).tab( 'show' );
				} else if ( $( '.product-single-collapse' ).length ) {
					$( '#product-reviews-content' ).collapse( 'show' );
				} else {
					return;
				}

				if ( $( '#product-reviews-content' ).length ) {
					setTimeout( function () {
						var scrollTabPos = $( '#product-reviews-content' ).offset().top - 60;

						$( 'html, body' ).stop().animate( {
							'scrollTop': scrollTabPos
						}, 800 );
					}, 250 );
				}
				e.preventDefault();
			} );
		},
		quantityInputs: function ( $wrap = 'body' ) {
			// Quantity input - cart - product pages
			if ( $.fn.TouchSpin ) {
				// Vertical Quantity
				$( $wrap ).find( '.vertical-quantity' ).TouchSpin( {
					verticalbuttons: true,
					verticalup: '',
					verticaldown: '',
					verticalupclass: 'icon-up-dir',
					verticaldownclass: 'icon-down-dir',
					buttondown_class: 'btn btn-outline',
					buttonup_class: 'btn btn-outline',
					initval: 1,
					min: 1
				} );

				// Horizontal Quantity
				$( $wrap ).find( '.horizontal-quantity' ).TouchSpin( {
					verticalbuttons: false,
					buttonup_txt: '',
					buttondown_txt: '',
					buttondown_class: 'btn btn-outline btn-down-icon',
					buttonup_class: 'btn btn-outline btn-up-icon',
					initval: 1,
					min: 1
				} );
			}
		},
		ajaxLoading: function () {
			$( 'body' ).append( "<div class='ajax-overlay'></div>" );
		},
		wordRotate: function () {
			if ( $.isFunction( $.fn[ 'themePluginWordRotator' ] ) ) {
				$( function () {
					$( '[data-plugin-wort-rotator], .wort-rotator:not(.manual)' ).each( function () {
						var $this = $( this ),
							opts = {};

						var pluginOptions = $this.data( 'plugin-options' );
						if ( pluginOptions )
							opts = pluginOptions;

						$this.themePluginWordRotator( opts );
					} );
				} );

			}
		},
		toggleFilter: function () {
			// toggle sidebar filter
			$( '.filter-toggle a' ).click( function ( e ) {
				e.preventDefault();
				$( '.filter-toggle' ).toggleClass( 'opened' );
				$( 'body' ).toggleClass( 'sidebar-opened' );
			} );

			// hide sidebar filter and sidebar overlay
			$( '.sidebar-overlay' ).click( function ( e ) {
				$( '.filter-toggle' ).removeClass( 'opened' );
				$( 'body' ).removeClass( 'sidebar-opened' );
			} );

			// show/hide sort menu
			$( '.sort-menu-trigger' ).click( function ( e ) {
				e.preventDefault();
				$( '.select-custom' ).removeClass( 'opened' );
				$( e.target ).closest( '.select-custom' ).toggleClass( 'opened' );
			} );
		},
		toggleSidebar: function () {
			$( '.sidebar-toggle' ).click( function ( e ) {
				e.preventDefault();
				$( 'body' ).toggleClass( 'sidebar-opened' );
			} );
		},
		toggleCart: function () {
			$( '.cart-toggle' ).click( function () {
				$( 'body' ).toggleClass( 'cart-opened' );
			} );

			$( '.btn-close' ).click( function () {
				$( 'body' ).toggleClass( 'cart-opened' );
			} );

			$( '.box-close' ).click( function () {
				var $this = $( this );
				$this.parent().remove();
			} );

			$( '.cart-overlay' ).click( function ( e ) {
				$( 'body' ).removeClass( 'cart-opened' );
			} );
		},
		scrollToElement: function () {
			$( '.scrolling-box a[href^="#"]' ).on( 'click', function ( event ) {
				var target = $( this.getAttribute( 'href' ) );

				if ( target.length ) {
					event.preventDefault();
					$( 'html, body' ).stop().animate( {
						scrollTop: target.offset().top - 69
					}, 700 );
				}
			} );
		},
		loginPopup: function () {
			$( '.login-link' ).click( function ( e ) {
				e.preventDefault();
				Porto.ajaxLoading();
				var ajaxUrl = "ajax/login-popup.html";

				$.magnificPopup.open( {
					type: 'ajax',
					mainClass: "login-popup",
					tLoading: '',
					preloader: false,
					removalDelay: 350,
					items: {
						src: ajaxUrl
					},
					callbacks: {
						open: function () {
							if ( $( '.sticky-header.fixed' ).css( 'padding-right' ) ) {
								$( '.sticky-header.fixed' ).css( 'padding-right', window.innerWidth - document.body.clientWidth );
								$( '.sticky-header.fixed-nav' ).css( 'padding-right', window.innerWidth - document.body.clientWidth );
								$( '#scroll-top' ).css( 'margin-right', window.innerWidth - document.body.clientWidth );
							}
						},
						beforeClose: function () {
							$( '.ajax-overlay' ).remove();
						},
						afterClose: function () {
							if ( $( '.sticky-header.fixed' ).css( 'padding-right' ) ) {
								$( '.sticky-header.fixed' ).css( 'padding-right', '' );
								$( '.sticky-header.fixed-nav' ).css( 'padding-right', '' );
								$( '#scroll-top' ).css( 'margin-right', '' );
							}
						}
					},
					ajax: {
						tError: '',
					}
				} );

			} );
		},
		productManage: function () {
			$( '.product-select' ).click( function ( e ) {
				$( this ).parents( '.product-default' ).find( 'figure img' ).attr( 'src', $( this ).data( 'src' ) );
				$( this ).addClass( 'checked' ).siblings().removeClass( 'checked' );
			} );
		},
		ratingTooltip: function () {
			$( 'body' ).on( 'mouseenter touchstart', '.product-ratings', function ( e ) {
				var ratingsRes = $( this ).find( '.ratings' ).width() / $( this ).width() * 5;
				$( this ).find( '.tooltiptext' ).text( ratingsRes ? ratingsRes.toFixed( 2 ) : ratingsRes );
			} );
		},
		windowClick: function () {
			$( document ).click( function ( e ) {
				// if click is happend outside of filter menu, hide it.
				if ( !$( e.target ).closest( '.toolbox-item.select-custom' ).length ) {
					$( '.select-custom' ).removeClass( 'opened' );
				}
			} );
		},
		popupMenu: function () {
			if ( $( '.popup-menu' ).length <= 0 ) {
				return;
			}

			// Hide scroll bar
			var $popup_menu_ul = $( '.popup-menu-ul' );
			var scroll_bar_size = $popup_menu_ul.parent().width() - $popup_menu_ul.children().width();
			scroll_bar_size >= 0 &&
				$popup_menu_ul.css( 'margin-right', '-' + scroll_bar_size + 'px' ),
				$popup_menu_ul.css( 'margin-top', scroll_bar_size + 'px' );

			// Open
			$( '.popup-menu-toggler' ).on( 'click', function ( e ) {
				e.preventDefault();
				$( this ).siblings( '.popup-menu' ).addClass( 'open' );

				// Close on escape key
				$( document ).on( 'keydown.popup-menu', function ( e ) {
					if ( e.which == 27 ) {
						$( '.popup-menu' ).removeClass( 'open' );
						$( document ).off( 'keydown.popup-menu' );
					}
				} );
			} );

			// Close when close button is clicked.
			$( 'body' ).on( 'click', '.popup-menu-close', function ( e ) {
				$( '.popup-menu' ).removeClass( 'open' );
				$( document ).off( 'keydown.popup-menu' );
				e.preventDefault();
			} );

			// Toggle submenus
			$( 'body' ).on( 'click', '.popup-menu-ul li a', function ( e ) {
				var $this = $( this ),
					$ul = $this.siblings( 'ul' );

				if ( $ul.length > 0 ) {
					$ul.length && $ul.toggleClass( 'open' ),
						e.preventDefault();
				}
			} );
		},
		topNotice: function () {
			if ( $( '.top-notice .mfp-close' ).length ) {
				$( 'body' ).on( 'click', '.top-notice .mfp-close', function () {
					var topNoticeHeight = $( this ).height();
					$( this )
						.closest( '.top-notice' )
						.fadeOut( function () {
							$( this ).addClass( 'closed' );
						} );
				} );
			}
		},
		ratingForm: function () {
			$( 'body' ).on( 'click', '.rating-form .rating-stars > a', function ( e ) {
				var $star = $( this );
				$star.addClass( 'active' ).siblings().removeClass( 'active' );
				$star.parent().addClass( 'selected' );
				$star.closest( '.rating-form' ).find( 'select' ).val( $star.text() );
				e.preventDefault();
			} );
		},

		parallax: function () {
			var wrappers = $( '[data-parallax]' ),
				defaultOptions = {
					speed: 1.5,
					horizontalPosition: '50%',
					offset: 0,
					enableOnMobile: true
				};

			if ( !wrappers.length ) {
				return;
			}

			wrappers.each( function () {
				var wrapper = $( this ),
					opts = wrapper.data( 'parallax' );

				if ( opts ) {
					opts = JSON.parse( opts.replace( /'/g, '"' ).replace( ';', '' ) );
				}

				var options = $.extend( true, {}, defaultOptions, opts ),
					$window = $( window ),
					offset,
					yPos,
					plxPos,
					background;

				// Create Parallax Element
				background = $( '<div class="parallax-background"></div>' );

				// Set Style for Parallax Element
				var bg = wrapper.data( 'image-src' ) ? 'url(' + wrapper.data( 'image-src' ) + ')' : wrapper.css( 'background-image' );
				background.css( {
					'background-image': bg,
					'background-size': 'cover',
					'background-position': '50% 0%',
					'position': 'absolute',
					'top': 0,
					'left': 0,
					'width': '100%',
					// 'height': '150%'
					'height': options.speed * 100 + '%'
				} );

				// Add Parallax Element on DOM
				wrapper.prepend( background );

				// Set Overlfow Hidden and Position Relative to Parallax Wrapper
				wrapper.css( {
					'position': 'relative',
					'overflow': 'hidden'
				} );

				// If enabled
				if ( !Porto.mobile || options.enableOnMobile ) {
					var moveParallax = function () {
						offset = wrapper.offset();
						yPos = -( $window.scrollTop() - ( offset.top - 100 ) ) / ( ( options.speed + 2 ) );
						plxPos = ( yPos < 0 ) ? Math.abs( yPos ) : -Math.abs( yPos );

						background.css( {
							'transform': 'translate3d(0, ' + ( ( plxPos - 50 ) + ( options.offset ) ) + 'px, 0)',
							'background-position-x': options.horizontalPosition
						} );
					};

					$( window ).on( 'scroll resize', moveParallax );
					moveParallax();

					// If disabled
				} else {
					wrapper.addClass( 'parallax-disabled' );
				}
			} );
		},

		isotopes: function () {
			var defaultOptions = {
				itemsSelector: '.grid-item',
				masonry: {
					columnWidth: '.grid-col-sizer'
				},
				percentPosition: true,

				// Sort
				sortBy: 'original-order',
				getSortData: {
					'md-order': '[data-md-order] parseInt'
				},
				sortReorder: false
			};

			// Init all grids
			$( '.grid' ).each( function () {
				var $this = $( this ),
					userOptions = $this.data( 'grid-options' );

				if ( userOptions ) {
					userOptions = JSON.parse( userOptions.replace( /'/g, '"' ).replace( ';', '' ) );
				}

				var options = $.extend( true, {}, defaultOptions, userOptions );
				var gridIns = $this.isotope( options );

				if ( typeof imagesLoaded === 'function' && $.fn.isotope ) {
					var imgLoad = imagesLoaded( '.grid', { background: true } );
					imgLoad.on( 'done', function ( instance, image ) {
						if ( window.innerWidth < 768 && window.innerWidth > 400 ) {
							gridIns.isotope( { sortBy: 'md-order' } );
						}

						//set big product height twice...
						var height_xl = $this.find( '.grid-item.height-xl' ).outerHeight();

						$this.find( '.grid-item.height-xxl' ).css( 'height', height_xl * 2 );

						$( window ).resize( function () {
							//set big product height twice...
							var height_xl = $this.find( '.grid-item.height-xl' ).outerHeight();

							$this.find( '.grid-item.height-xxl' ).css( 'height', height_xl * 2 );
							gridIns.isotope( 'layout' );
						} )

						gridIns.isotope( 'layout' );
					} );
				}

				if ( options.sortReorder ) {
					var reorderGrid = function () {
						var windowWidth = $( window ).width();
						gridIns.isotope( {
							sortBy: ( windowWidth < 768 && windowWidth > 400 ) ?
								'md-order' :
								'original-order'
						} );
					};

					// Reorder when resize event is called.
					$.fn.smartresize ?
						$( window ).smartresize( reorderGrid ) :
						$( window ).on( 'resize', reorderGrid );
				}
			} );
		},
		zoomImage: function () {
			var initZoom = function () {
				$( '.product-single-grid .product-single-gallery img' ).each( function () {
					var $this = $( this ),
						zoomConfig = {
							responsive: true,
							zoomWindowFadeIn: 350,
							zoomWindowFadeOut: 200,
							borderSize: 0,
							zoomContainer: $this.parent(),
							zoomType: 'inner',
							cursor: 'grab'
						};
					$this.elevateZoom( zoomConfig );
				} );
			};

			$( window ).resize( initZoom );
		},
		sideMenu: function () {
			$( '.side-menu' ).length &&
				$( 'body' ).on( 'click', '.side-menu-toggle', function ( e ) {
					$( this ).siblings( 'ul' ).slideToggle( 'fast' );
					$( this ).parent().toggleClass( 'show' );
					e.stopPropagation();
				} );
		},
		productsCartAction: function () {
			// Add to cart in products
			$( 'body' ).on( 'click', '.btn-add-cart.product-type-simple', function ( e ) {
				e.preventDefault();
				var $product;

				if ( $( this ).closest( '.product-default' ).length > 0 ) {
					$product = $( this ).closest( '.product-default' );
				} else {
					$product = $( this ).closest( '.product-row' );
				}

				Porto.miniPopup.open( {
					name: $product.find( '.product-title' ).text(),
					nameLink: $product.find( '.product-title > a' ).attr( 'href' ),
					imageSrc: $product.find( 'figure img' ).attr( 'src' ),
					imageLink: $product.find( '.product-title > a' ).attr( 'href' )
				} );
			} );
		},
		productsWishlistAction: function () {
			// Add to wishlist in products
			$( 'body' ).on( 'click', '.btn-icon-wish:not(.added-wishlist)', function ( e ) {
				e.preventDefault();
				var $this = $( this );

				$this.addClass( 'load-more-overlay loading' );

				setTimeout( function () {
					$this.removeClass( 'load-more-overlay loading' );
					$this.addClass( 'added-wishlist ' );

					if ( $this.find( 'span' ).text() !== '' ) {
						$this.find( 'span' ).text( 'BROWSE WISHLIST' );
					}

					$this.attr( 'title', 'Go to Wishlist' );

					$( '.wishlist-popup' ).addClass( 'active' );
				}, 1000 );

				setTimeout( function () {
					$( '.wishlist-popup' ).removeClass( 'active' );
				}, 3000 );
			} )
		},

		initPurchasedMinipopup: function () {
			if ( $( '.product-single' ).length || $( '.main-content' ).length ) {
				setInterval( function () {
					Porto.miniPopup.open( {
						name: 'Mobile Speaker',
						nameLink: 'product.html',
						imageSrc: 'assets/images/products/small/product-1.jpg',
						content: 'Someone Purchased',
						action: '<span class="text-primary" style="font-size: 11px;">12 MINUTES AGO</span>',
					} );
				}, 60000 );
			}
		},
		initJqueryParallax: function () {
			$( '.home-slider ul.scene' ).parallax();
			$( '.bg-parallax ul.scene' ).parallax();
		},
		ajaxLoadProducts: function () {
			var loadCount = 0;
			$( '.loadmore' ).click( function ( e ) {
				e.preventDefault();
				var $this = $( this );
				var originText = $this.text();
				$this.text( 'Loading ...' );
				$.ajax( {
					url: $this.attr( 'href' ),
					success: function ( result ) {
						var grid = $( '.product-ajax-grid' );

						var $newItems = $( result );

						setTimeout( function () {
							$newItems.hide().appendTo( grid ).fadeIn();
							$this.text( originText );
							++loadCount >= 2 && $this.hide();
						}, 350 );
					},
					failure: function () {
						$this.text( "Sorry something went wrong." );
					}
				} );
			} );
		},
		categoryNavScroll: function () {
			// Smooth scroll
			$( '.category-list-nav' ).find( 'a' ).on( 'click', function ( e ) {
				var targetId = $( this ).attr( 'href' ),
					target = $( targetId );

				if ( target.length ) {
					var targetPos = target.offset().top - 70;

					$( 'html, body' ).animate( {
						'scrollTop': targetPos
					}, 700 );
					e.preventDefault();
				}
			} );
		},
		footerReveal: function () {
			var $footer = $( '.footer-reveal' );
			if ( $footer.length ) {
				var fixFooter = function () {
					if ( $( window ).width() >= 992 ) {
						$footer.parent().css( 'margin-bottom', $footer.outerHeight() );
						$footer.css( 'position', 'fixed' );
					} else {
						$footer.parent().css( 'margin-bottom', '' );
						$footer.css( 'position', 'static' );
					}
				}

				$( window ).resize( fixFooter );

				fixFooter();
			}
		},
		intObs: function ( selector, functionName, intObsOptions, alwaysObserve ) {
			var $el = document.querySelectorAll( selector );
			var intersectionObserverOptions = {
				rootMargin: '0px 0px 200px 0px'
			}

			if ( Object.keys( intObsOptions ).length ) {
				intersectionObserverOptions = $.extend( intersectionObserverOptions, intObsOptions );
			}

			var observer = new IntersectionObserver( function ( entries ) {
				for ( var i = 0; i < entries.length; i++ ) {
					var entry = entries[ i ];

					if ( entry.intersectionRatio > 0 ) {
						if ( typeof functionName === 'string' ) {
							var func = Function( 'return ' + functionName )();
						} else {
							var callback = functionName;

							callback.call( $( entry.target ) );
						}

						// Unobserve
						if ( !alwaysObserve ) {
							observer.unobserve( entry.target );
						}

					}
				}
			}, intersectionObserverOptions );

			$( $el ).each( function () {
				observer.observe( $( this )[ 0 ] );
			} );
		}
	};

	/**
	 * @function initProductSingle
	 *
	 * @param {jQuery} $el
	 * @param {object} options
	 *
	 * @requires OwlCarousel
	 * @requires ImagesLoaded (only quickview needs)
	 * @requires elevateZoom
	 * @instance multiple
	 */

	Porto.initProductSingle = ( function () {
		/**
		 * @class ProductSingle
		 */
		function ProductSingle( $el ) {
			return this.init( $el );
		}

		var thumbsInit = function ( self ) {
			if ( self.$wrapper.find( '.product-thumbs' ).length > 0 ) {
				self.$thumbs = self.$wrapper.find( '.product-thumbs' );
			} else {
				self.$thumbs = self.$wrapper.find( '.prod-thumbnail' );
			}

			self.$thumbsWrap = self.$thumbs.parent();
			self.$thumbsDots = self.$thumbs.children();
			self.$thumbsCount = self.$thumbsDots.length;
			self.$thumbUp = self.$thumbsWrap.parent().find( '.thumb-up' );
			self.$thumbDown = self.$thumbsWrap.parent().find( '.thumb-down' );
			self.$productThumb = self.$thumbsDots.eq( 0 );

			// register events
			self.$thumbUp.on( 'click', function ( e ) {
				self.$isPgVertical && thumbsUp( self );
			} );

			self.$thumbDown.on( 'click', function ( e ) {
				self.$isPgVertical && thumbsDown( self );

			} );

			self.$thumbsDots.on( 'click', function () {
				var $this = $( this ),
					index = ( $this.parent().filter( self.$thumbs ).length ? $this : $this.parent() ).index();

				self.$wrapper.find( '.product-single-carousel' ).trigger( 'to.owl.carousel', index );
			} );

			// refresh thumbs
			thumbsRefresh( self );
			$( window ).on( 'resize', function () {
				thumbsRefresh( self );
			} );
		}

		var thumbsUp = function ( self ) {
			var maxTop = self.$thumbsWrap.offset().top,
				curTop = self.$thumbs.offset().top;

			if ( curTop <= maxTop - self.$productThumb[ 0 ].offsetHeight ) {
				self.$thumbs.css( 'top', parseInt( self.$thumbs.css( 'top' ) ) + self.$productThumb[ 0 ].offsetHeight );
				self.$thumbDown.removeClass( 'disabled' );
			} else if ( curTop < maxTop ) {
				self.$thumbs.css( 'top', parseInt( self.$thumbs.css( 'top' ) ) - Math.ceil( curTop - maxTop ) );
				self.$thumbDown.removeClass( 'disabled' );
				self.$thumbUp.addClass( 'disabled' );
			} else {
				self.$thumbUp.addClass( 'disabled' );
			}
		}

		var thumbsDown = function ( self ) {
			var maxBottom = self.$thumbsWrap.offset().top + self.$thumbsWrap[ 0 ].offsetHeight,
				curBottom = self.$thumbs.offset().top + self.$thumbsHeight;

			if ( curBottom >= maxBottom + self.$productThumb[ 0 ].offsetHeight ) {
				self.$thumbs.css( 'top', parseInt( self.$thumbs.css( 'top' ) ) - self.$productThumb[ 0 ].offsetHeight );
				self.$thumbUp.removeClass( 'disabled' );
			} else if ( curBottom > maxBottom ) {
				self.$thumbs.css( 'top', parseInt( self.$thumbs.css( 'top' ) ) - Math.ceil( curBottom - maxBottom ) );
				self.$thumbUp.removeClass( 'disabled' );
				self.$thumbDown.addClass( 'disabled' );
			} else {
				self.$thumbDown.addClass( 'disabled' );
			}
		}

		var thumbsRefresh = function ( self ) {
			var sliderThumb = {
				margin: 8,
				items: 4,
				dots: false,
				nav: true,
				navText: [ '<i class="fas fa-chevron-left">', '<i class="fas fa-chevron-right">' ]
			}

			if ( typeof self.$thumbs == 'undefined' ) {
				return;
			}

			if ( self.$isPgVertical.length > 0 ) {
				// disable thumbs carousel
				self.$thumbsHeight = self.$productThumb[ 0 ].offsetHeight * self.$thumbsCount + parseInt( self.$productThumb.css( 'margin-bottom' ) ) * ( self.$thumbsCount - 1 );
				self.$thumbUp.addClass( 'disabled' );
				self.$thumbDown.toggleClass( 'disabled', self.thumbsHeight <= self.$thumbsWrap[ 0 ].offsetHeight );
			} else {
				// enable thumbs carousel
				if ( !self.$thumbs.hasClass( 'owl-carousel' ) && !self.$thumbs.hasClass( 'transparent-dots' ) && !self.$thumbs.hasClass( 'thumb-vertical' ) ) {
					self.$thumbs.addClass( 'owl-carousel owl-theme show-nav-hover' ).owlCarousel(
						$.extend(
							true,
							self.isQuickview ? {
							} : {},
							sliderThumb
						) );
				}
			}
		}

		var initVariation = function ( self ) {
			self.$selects = self.$wrapper.find( '.product-single-filter select' );
			self.$items = self.$wrapper.find( '.product-single-filter:not(:last-child)' );
			self.$priceWrap = self.$wrapper.find( '.product-filtered-price' );
			self.$clean = self.$wrapper.find( '.product-single-filter:last-child' );
			self.$btnCart = self.$wrapper.find( '.add-cart' );
			self.$btnView = self.$wrapper.find( '.view-cart' );
			self.$cartMessage = self.$wrapper.find( '.cart-message' );

			// check
			self.variationCheck();
			self.$selects.on( 'change', function ( e ) {
				self.variationCheck();
			} );

			self.$items.find( 'li' ).on( 'click', function ( e ) {

				if ( !$( this ).children().hasClass( 'disabled' ) ) {
					$( this ).toggleClass( 'active' ).siblings().removeClass( 'active' );
					e.preventDefault();
					self.variationCheck();
				}
			} );

			// clean
			self.$clean.find( '.clear-btn' ).on( 'click', function ( e ) {
				e.preventDefault();
				self.variationClean( true );
			} );

			self.$btnCart.on( 'click', function ( e ) {
				e.preventDefault();

				if ( !self.$btnCart.hasClass( 'disabled' ) ) {
					self.$btnCart.addClass( 'added-to-cart' );
					self.$btnView.removeClass( 'd-none' );
					self.$cartMessage.removeClass( 'd-none' );
				}
			} );
		}

		ProductSingle.prototype.init = function ( $el ) {
			var self = this,
				$slider = $el.find( '.product-single-carousel' );
			// members
			self.$wrapper = $el;
			self.isQuickview = !!$el.closest( '.mfp-content' ).length;
			self.$isPgVertical = self.$wrapper.find( '.pg-vertical' );
			$el.find( '.owl-dots' ).children().eq( 0 ).addClass( 'active' );

			// init thumbs

			$slider.on( 'initialized.owl.carousel', function () {
				thumbsInit( self );
			} ).on( 'translate.owl.carousel', function ( e ) {
				var currentIndex = ( e.item.index - $( e.currentTarget ).find( '.cloned' ).length / 2 + e.item.count ) % e.item.count;
				self.thumbsSetActive( currentIndex );
			} );

			initVariation( this );
		}

		ProductSingle.prototype.thumbsSetActive = function ( index ) {
			var self = this,
				$curThumb = self.$thumbsDots.eq( index );

			self.$thumbsDots.filter( '.active' ).removeClass( 'active' );
			$curThumb.addClass( 'active' );

			// show current thumb
			if ( self.$isPgVertical.length > 0 ) { // if vertical
				var offset = parseInt( self.$thumbs.css( 'top' ) ) + index * self.$thumbsHeight;
				if ( offset < 0 ) {
					// if above
					if ( !self.$thumbUp.hasClass( 'disabled' ) ) {
						self.$thumbUp.addClass( 'disabled' );
					}

					if ( self.$thumbDown.hasClass( 'disabled' ) ) {
						self.$thumbDown.removeClass( 'disabled' )
					};

					self.$thumbs.css( 'top', parseInt( self.$thumbs.css( 'top' ) ) - offset );
				} else {
					offset = self.$thumbs.offset().top + self.$thumbs[ 0 ].offsetHeight - $curThumb.offset().top - $curThumb[ 0 ].offsetHeight;

					if ( offset < 0 ) {
						// if below
						if ( self.$thumbUp.hasClass( 'disabled' ) ) {
							self.$thumbUp.removeClass( 'disabled' )
						};

						if ( !self.$thumbDown.hasClass( 'disabled' ) ) {
							self.$thumbDown.addClass( 'disabled' );
						}

						self.$thumbs.css( 'top', parseInt( self.$thumbs.css( 'top' ) ) + offset );
					}
				}
			} else { // if thumb carousel
				self.$thumbs.trigger( 'to.owl.carousel', index, 100 );
			}
		}

		ProductSingle.prototype.variationCheck = function () {
			var self = this,
				isAllSelected = true;

			// check all item variations are selected
			// check all select variations are selected
			self.$selects.each( function () {
				return this.value || ( isAllSelected = false );
			} );

			self.$items.each( function () {
				var $this = $( this );

				if ( $this.find( 'li' ).length ) {
					$this.find( '.active' ).length || ( isAllSelected = false );
				}
			} );

			isAllSelected ?
				self.variationMatch() :
				self.variationClean();
		}

		ProductSingle.prototype.variationMatch = function () {
			var self = this;
			self.$priceWrap.find( 'span' ).text( '$' + ( Math.round( Math.random() * 50 ) + 170 ) + '.00' );
			self.$priceWrap.find( 'del span' ).text( '$' + ( Math.round( Math.random() * 50 ) + 200 ) + '.00' );
			self.$priceWrap.slideDown();
			self.$clean.slideDown();
			self.$btnCart.removeClass( 'disabled' );
		}

		ProductSingle.prototype.variationClean = function ( reset ) {
			reset && this.$items.find( '.active' ).removeClass( 'active' );
			reset && this.$selects.val( '' );

			if ( this.$btnCart.hasClass( 'added-to-cart' ) ) {
				this.$btnCart.removeClass( 'added-to-cart' );
			}

			this.$priceWrap.slideUp();
			this.$clean.slideUp();

			this.$btnView.addClass( 'd-none' );
			this.$cartMessage.addClass( 'd-none' );

			if ( this.$wrapper.find( '.product-single-filter' ).length > 0 ) {
				this.$btnCart.addClass( 'disabled' );
			}
		}

		return function ( $el, options ) {
			if ( $el ) {
				return new ProductSingle( $el.eq( 0 ), options );
			}

			return null;
		}
	} )();

	Porto.initProductSinglePage = ( function () {
		var $product;

		// Public Properties
		return function () {
			$product = $( '.product-single-container' );

			if ( $product.length ) {
				if ( Porto.initProductSingle( $product ) === null ) {
					return null;
				}
			} else {
				// if no single product exists, return
				return null;
			}
		}
	} )();

	/**
	 * @class MiniPopup
	 */
	Porto.miniPopup = ( function () {
		var $area,
			offset = 0,
			boxes = [],
			isPaused = false,
			timers = [],
			timerId = false,
			timerInterval = 200,
			timerClock = function () {
				if ( isPaused ) {
					return;
				}
				for ( var i = 0; i < timers.length; ++i ) {
					( timers[ i ] -= timerInterval ) <= 0 && this.close( i-- );
				}
			}

		return {
			init: function () {
				// init area for cart popup
				var area = document.createElement( 'div' );
				area.className = "minipopup-area";
				$( '.page-wrapper' )[ 0 ].appendChild( area );

				$area = $( area );
				$area.on( 'click', '.mfp-close', function ( e ) {
					self.close( $( this ).closest( '.minipopup-box' ).index() );
				} );

				// init area for wishlsit popup
				var areaWishlistPopup = document.createElement( 'div' );
				var areaWishlistPopupMsg = document.createElement( 'div' );
				areaWishlistPopup.className = "wishlist-popup";
				areaWishlistPopupMsg.className = "wishlist-popup-msg";
				areaWishlistPopupMsg.innerText = "Product added!";
				$( '.page-wrapper' )[ 0 ].appendChild( areaWishlistPopup );
				$( '.wishlist-popup' ).append( areaWishlistPopupMsg );

				// bind methods
				this.close = this.close.bind( this );
				timerClock = timerClock.bind( this );
			},

			open: function ( options, callback ) {
				var self = this,
					settings = $.extend( true, {}, Porto.minipopup, options ),
					$box;

				$box = $( parseTemplate( settings.template, settings ) );

				self.space = settings.space;

				// open
				$box
					.appendTo( $area )
					.css( 'top', - offset )
					.find( "img" )[ 0 ]
					.onload = function () {
						offset += $box[ 0 ].offsetHeight + self.space;

						$box.addClass( 'active' );
						if ( $box.offset().top - window.pageYOffset < 0 ) {
							self.close();
							$box.css( 'top', - offset + $box[ 0 ].offsetHeight + self.space );
						}
						$box.on( 'mouseenter', function () { self.pause() } )
							.on( 'mouseleave', function () { self.resume() } )
							.on( 'touchstart', function ( e ) { self.pause(); e.stopPropagation(); } )
							.on( 'mousedown', function () {
								$( this ).addClass( 'focus' );
							} )
							.on( 'mouseup', function () {
								self.close( $( this ).index() );
							} );
						$( 'body' ).on( 'touchstart', function () {
							self.resume();
						} );

						boxes.push( $box );
						timers.push( settings.delay );

						( timers.length > 1 ) || (
							timerId = setInterval( timerClock, timerInterval )
						)

						callback && callback( $box );
					};
			},

			close: function ( indexToClose ) {
				var self = this,
					index = ( 'undefined' === typeof indexToClose ) ? 0 : indexToClose,
					$box = boxes.splice( index, 1 )[ 0 ];

				// remove timer
				timers.splice( index, 1 )[ 0 ];

				// remove box
				offset -= $box[ 0 ].offsetHeight + self.space;
				$box.removeClass( 'active' );
				setTimeout( function () {
					$box.remove();
				}, 300 );

				// slide down other boxes
				boxes.forEach( function ( $box, i ) {
					if ( i >= index && $box.hasClass( 'active' ) ) {
						$box.stop( true, true ).animate( {
							top: parseInt( $box.css( 'top' ) ) + $box[ 0 ].offsetHeight + 20
						}, 600, 'easeOutQuint' );
					}
				} );

				// clear timer
				boxes.length || clearTimeout( timerId );
			},

			pause: function () {
				isPaused = true;
			},

			resume: function () {
				isPaused = false;
			}
		}
	} )();

	/**
	 * Parse html template with variables.
	 * @param {string} template
	 * @param {object} vars
	 */
	var parseTemplate = function ( template, vars ) {
		return template.replace( /\{\{(\w+)\}\}/g, function () {
			return vars[ arguments[ 1 ] ];
		} );
	}

	// Ready Event
	jQuery( document ).ready( function (
	) {
		// Init our app
		Porto.init();
		setTimeout( () => {
			Porto.appearAnimate();
			Porto.scrollBtnAppear();
		}, 500 );
	} );

	$( window ).on( 'load', function () {
		$( 'body' ).addClass( "loaded" );
	} );
	// Load Event

	// Scroll Event
	$( window ).on( 'scroll', function () {
		Porto.scrollBtnAppear();
	} );
} )( jQuery );

